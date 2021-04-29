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

        /**
         * aes key
         */
        private lateinit var secretKey: ByteArray

        /**
         * key tag
         */
        private var hasKey = false

    }

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        val inStream = chain.request()
        val rpcObject = inStream.rpcObject ?: return inStream

        //exchange secret key
        if (!hasKey) {
            synchronized(hasKey) {
                if (!hasKey) {

                    log { "exchange secret key" }

                    //send 01 , ask exchange secret key
                    val requestPublicKey = ByteArray(2) { it.toByte() }
                    inStream.body = requestPublicKey
                    var outStream = chain.proceed(inStream)
                    val publicKeyBody = outStream.body ?: ByteArray(0)
                    if (publicKeyBody.isEmpty()) {
                        outStream.result = Unit
                        return outStream
                    }
                    publicKey = RSAUtil.getPublicKey(publicKeyBody)

                    //send 10 and send aes
                    inStream.body = ByteArray(2) { arrayOf(1.toByte(), 0.toByte())[it] }
                    chain.proceed(inStream)
                    secretKey = AESUtil.generateKey()
                    val secretAESBody = RSAUtil.encryptByPublicKey(secretKey, publicKey)
                    inStream.body = secretAESBody
                    outStream = chain.proceed(inStream)
                    val response = outStream.body ?: ByteArray(0)
                    if (response.isEmpty()) {
                        outStream.result = Unit
                        return outStream
                    }
                    //receive exchange result , 0b11:success
                    if (response.size == 2 && response[0] == 1.toByte() && response[1] == 1.toByte()) {
                        hasKey = true
                    } else {
                        outStream.result = Unit
                        return outStream
                    }

                    log { "exchange secret key end , hasKey:$hasKey" }

                }
            }
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