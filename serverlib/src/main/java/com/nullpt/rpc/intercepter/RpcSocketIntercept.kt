package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * rpc socket layer
 */
class RpcSocketIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        //accept socket
        val inStream = chain.request()
        val socket = inStream.socket

        val inputStream = socket.getInputStream()
        val objectInputStream = ObjectInputStream(inputStream)
        val requestBody = objectInputStream.readObject()
        inStream.secretBody = if (requestBody is ByteArray) {
            requestBody
        } else {
            ByteArray(0)
        }

        val outStream = chain.proceed(inStream)
        val result = outStream.secretBody ?: ByteArray(0)

        outStream.result = result.isNotEmpty()

        val outputStream = socket.getOutputStream()
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(result)
        objectOutputStream.flush()

        return outStream

    }
}