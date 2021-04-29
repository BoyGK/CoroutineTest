package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.RpcStream
import com.nullpt.rpc.log
import java.io.*

/**
 * rpc secret params
 * read real object from inputStream and transfer it
 * return encrypt object
 */
internal class RpcSecretIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        val inStream = chain.request()
        val rpcObject = inStream.rpcObject ?: return inStream
        //encrypt
        inStream.secretBody = encrypt(rpcObject)
        val outStream = chain.proceed(inStream)

        val secretBody = outStream.secretBody ?: ByteArray(0)
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
    private fun decrypt(rpcResponseObject: ByteArray): Any {
        val byteArrayInputStream = ByteArrayInputStream(rpcResponseObject)
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