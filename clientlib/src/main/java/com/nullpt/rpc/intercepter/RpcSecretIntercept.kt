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

        private var hasKey = false

    }

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        val inStream = chain.request()
        val rpcObject = inStream.rpcObject ?: return inStream

        //encrypt
        if (!hasKey) {
            var outStream: RpcStream

            //协商公钥 send 01
            val requestPublicKey = ByteArray(2) { it.toByte() }
            inStream.body = requestPublicKey
            outStream = chain.proceed(inStream)
            val publicKeyBody = outStream.body ?: ByteArray(0)
            if (publicKeyBody.isEmpty()) {
                outStream.result = Unit
                return outStream
            }
            publicKey = RSAUtil.getPublicKey(publicKeyBody)

            //创建AES
            secretKey = AESUtil.generateKey()
            val secretAESBody = RSAUtil.encryptByPublicKey(secretKey, publicKey)
            inStream.body = secretAESBody
            outStream = chain.proceed(inStream)
            val response = outStream.body ?: ByteArray(0)
            if (response.isEmpty()) {
                outStream.result = Unit
                return outStream
            }
            //receive 0b11
            if (response.size == 2 && response[0] == 1.toByte() && response[1] == 1.toByte()) {
                hasKey = true
            } else {
                outStream.result = Unit
                return outStream
            }

        }

        //aes 加密
        inStream.body = encrypt(rpcObject)
        val outStream = chain.proceed(inStream)

        val secretBody = outStream.body ?: ByteArray(0)
        if (secretBody.isEmpty()) {
            outStream.result = Unit
            return outStream
        }
        //decrypt
        outStream.result = decrypt(secretBody)
        return outStream
    }

    /**
     * assume decrypt
     */
    private fun decrypt(byteArray: ByteArray): Any {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        return objectInputStream.readObject()
    }

    /**
     * assume encrypt
     */
    private fun encrypt(rpcObject: RpcObject): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(rpcObject)
        objectOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }
}