package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket

/**
 * rpc socket layer
 */
class RpcSocketIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        //create socket
        val serverSocket = chain.request() as ServerSocket
        val socket = serverSocket.accept()

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

        //no socket cache just now
        if (socket.isConnected) {
            socket.shutdownInput()
            socket.shutdownOutput()
        }
        inputStream.close()
        outputStream.close()
        objectInputStream.close()
        objectOutputStream.close()
        socket.close()

        return true

    }
}