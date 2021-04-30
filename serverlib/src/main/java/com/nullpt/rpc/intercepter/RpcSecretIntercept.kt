package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.RpcStream
import com.nullpt.rpc.encryption.AESUtil
import com.nullpt.rpc.encryption.RSAUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.security.PrivateKey
import java.security.PublicKey

/**
 * rpc secret params
 * read real object from inputStream and transfer it
 * return encrypt object
 */
class RpcSecretIntercept : RpcIntercept {

    companion object {
        /**
         * rsa key pair
         */
        private val keyPair: Pair<PrivateKey, PublicKey> = RSAUtil.generateKey()

        private const val SUCCESS = "success"
        private const val ERROR = "error"
    }

    /**
     * aes key
     */
    private lateinit var secretKey: ByteArray

    /**
     * receive tag
     */
    private var receiveAES = false

    override fun next(chain: RpcIntercept.Chain): RpcStream {

        val inStream = chain.request()
        val requestBody = inStream.body ?: ByteArray(0)
        if (requestBody.isEmpty()) {
            inStream.body = ByteArray(0)
            return inStream
        }

        if (requestBody.size == 2 && requestBody[0] == 0.toByte() && requestBody[1] == 1.toByte()) {
            //send public key
            inStream.body = keyPair.second.encoded
            return inStream
        } else if (requestBody.size == 2 && requestBody[0] == 1.toByte() && requestBody[1] == 0.toByte()) {
            //receive aes
            receiveAES = true
            inStream.body = ByteArray(2) { arrayOf(1.toByte(), 1.toByte())[it] }
            return inStream
        } else if (receiveAES) {
            receiveAES = false
            //decrypt aes
            secretKey = RSAUtil.decryptByPrivateKey(requestBody, keyPair.first)
            if (secretKey.isEmpty()) {
                inStream.body = ERROR.toByteArray()
            } else {
                inStream.body = SUCCESS.toByteArray()
            }
            return inStream
        } else {
            //secret message
            val decryptBody = AESUtil.decrypt(requestBody, secretKey)
            val rpcObject = bytes2Object(decryptBody) ?: return inStream
            inStream.rpcObject = rpcObject
            val outStream = chain.proceed(inStream)
            //encrypt
            outStream.body = AESUtil.encrypt(object2bytes(outStream.result), secretKey)
            return outStream
        }
    }

    /**
     * bytes2Object
     */
    private fun bytes2Object(secretBody: ByteArray): RpcObject? {
        if (secretBody.isEmpty()) {
            return null
        }
        val byteArrayInputStream = ByteArrayInputStream(secretBody)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        val rpcObject = objectInputStream.readObject()
        return if (rpcObject is RpcObject) {
            rpcObject
        } else {
            null
        }
    }

    /**
     * object2bytes
     */
    private fun object2bytes(result: Any?): ByteArray {
        if (result == null || result is Unit) {
            return ByteArray(0)
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(result)
        objectOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }
}