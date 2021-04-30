package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.RpcStream
import com.nullpt.rpc.encryption.AESUtil
import com.nullpt.rpc.encryption.RSAUtil
import com.nullpt.rpc.log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.security.PublicKey

/**
 * rpc secret params
 * read real object from inputStream and transfer it
 * return encrypt object
 */
internal class RpcSecretIntercept : RpcIntercept {

    companion object {
        /**
         * rsa key pair[publicKey]
         */
        private lateinit var publicKey: PublicKey
        private var hasPublicKey = false

        private const val SUCCESS = "success"
        private const val ERROR = "error"
    }

    /**
     * aes key
     */
    private lateinit var secretKey: ByteArray

    private var hasKey = false

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        val inStream = chain.request()
        val rpcObject = inStream.rpcObject ?: return inStream

        //exchange secret key
        if (!hasKey) {

            log { "exchange secret key" }

            synchronized(hasPublicKey) {
                if (!hasPublicKey) {
                    log { "exchange public key" }
                    //send 01 , ask exchange secret key once
                    val requestPublicKey = ByteArray(2) { it.toByte() }
                    inStream.body = requestPublicKey
                    val outStream = chain.proceed(inStream)
                    val responseBody = outStream.body ?: ByteArray(0)
                    if (responseBody.isEmpty()) {
                        outStream.result = Unit
                        return outStream
                    }
                    publicKey = RSAUtil.getPublicKey(responseBody)
                    hasPublicKey = true
                }
            }

            log { "ready to aes key" }
            //send 10 , to accept aes
            inStream.body = ByteArray(2) { arrayOf(1.toByte(), 0.toByte())[it] }
            var outStream = chain.proceed(inStream)
            var responseBody = outStream.body ?: ByteArray(0)
            if (responseBody.isEmpty()) {
                outStream.result = Unit
                return outStream
            }
            if (responseBody.size != 2 || responseBody[0] != 1.toByte() || responseBody[1] != 1.toByte()) {
                outStream.result = Unit
                return outStream
            }
            log { "ready to aes key success" }

            log { "exchange aes key" }
            //send ase key by pub encrypt
            secretKey = AESUtil.generateKey()
            inStream.body = RSAUtil.encryptByPublicKey(secretKey, publicKey)
            outStream = chain.proceed(inStream)
            responseBody = outStream.body ?: ByteArray(0)
            if (responseBody.isEmpty()) {
                outStream.result = Unit
                return outStream
            }

            //receive exchange result
            if (String(responseBody) == SUCCESS) {
                hasKey = true
                log { "exchange aes key success" }
            } else {
                hasKey = false
                log { "exchange aes key error" }
                outStream.result = Unit
                return outStream
            }

            log { "exchange secret key end , hasKey:$hasKey" }

        }

        //aes encrypt and send
        inStream.body = AESUtil.encrypt(object2Bytes(rpcObject), secretKey)
        val outStream = chain.proceed(inStream)

        val secretBody = outStream.body ?: ByteArray(0)
        if (secretBody.isEmpty()) {
            outStream.result = Unit
            return outStream
        }
        //decrypt to result
        outStream.result = byte2Object(AESUtil.decrypt(secretBody, secretKey))
        return outStream
    }

    /**
     * byte2Object
     */
    private fun byte2Object(byteArray: ByteArray): Any {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        return objectInputStream.readObject()
    }

    /**
     * object2Bytes
     */
    private fun object2Bytes(rpcObject: RpcObject): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(rpcObject)
        objectOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }
}