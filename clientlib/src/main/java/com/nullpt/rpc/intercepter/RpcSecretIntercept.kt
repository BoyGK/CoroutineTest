package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.log
import java.io.*

/**
 * rpc secret params
 * read real object from inputStream and transfer it
 * return encrypt object
 */
internal class RpcSecretIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        val rpcObject = chain.request() as RpcObject
        //encrypt
        val result = chain.proceed(encrypt(rpcObject))
        if (result is Unit) {
            return Unit
        }
        //decrypt
        return decrypt(result)
    }

    /**
     * assume decrypt
     */
    private fun decrypt(rpcResponseObject: Any): Any {
        if (rpcResponseObject !is ByteArray) {
            log {
                "decrypt error!"
            }
            return Unit
        }
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