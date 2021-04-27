package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

/**
 * rpc socket layer
 */
class RpcSocketIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        //accept socket
        val socket = chain.request() as Socket

        val inputStream = socket.getInputStream()
        val objectInputStream = ObjectInputStream(inputStream)

        val result = chain.proceed(objectInputStream.readObject())
        if (result is Unit) {
            return false
        }

        val outputStream = socket.getOutputStream()
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(result)
        objectOutputStream.flush()

        return true

    }
}