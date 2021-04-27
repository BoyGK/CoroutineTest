package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.log
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

    override fun next(chain: RpcIntercept.Chain): Any {
        //decrypt
        val rpcObject = decrypt(chain.request())
        if (rpcObject is Unit) {
            return Unit
        }
        val result = chain.proceed(rpcObject)
        //encrypt
        return encrypt(result)
    }

    /**
     * assume decrypt
     */
    private fun decrypt(rpcRequestObject: Any): Any {
        if (rpcRequestObject !is ByteArray) {
            log {
                "decrypt error!"
            }
            return Unit
        }
        val byteArrayInputStream = ByteArrayInputStream(rpcRequestObject)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        return objectInputStream.readObject() as RpcObject
    }

    /**
     * assume encrypt
     */
    private fun encrypt(result: Any): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(result)
        objectOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }
}