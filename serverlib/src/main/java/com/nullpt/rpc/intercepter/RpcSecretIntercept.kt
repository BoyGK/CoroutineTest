package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.RpcStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * rpc secret params
 * read real object from inputStream and transfer it
 * return encrypt object
 */
class RpcSecretIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        //decrypt
        val inStream = chain.request()
        val secretBody = inStream.secretBody ?: ByteArray(0)
        inStream.secretBody = null
        val rpcObject = decrypt(secretBody) ?: return inStream
        inStream.rpcObject = rpcObject
        val outStream = chain.proceed(inStream)
        //encrypt
        outStream.secretBody = encrypt(outStream.result)
        return outStream
    }

    /**
     * assume decrypt
     */
    private fun decrypt(secretBody: ByteArray): RpcObject? {
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
     * assume encrypt
     */
    private fun encrypt(result: Any?): ByteArray {
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