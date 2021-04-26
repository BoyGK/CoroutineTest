package com.nullpt.rpc.intercepter

import com.nullpt.rpc.Config
import com.nullpt.rpc.RpcIntercept
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

/**
 * rpc socket layer
 */
class RpcSocketIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        var socket: Socket? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        try {
            //create socket ,and connect
            socket = Socket()
            socket.connect(InetSocketAddress(Config.ip, Config.port), Config.timeout)
            outputStream = socket.getOutputStream()
            inputStream = socket.getInputStream()
            val objectInputStream = ObjectInputStream(inputStream)
            val objectOutputStream = ObjectOutputStream(outputStream)

            val rpcRequestObject = chain.request()
            objectOutputStream.writeObject(rpcRequestObject)
            objectOutputStream.flush()

            return objectInputStream.readObject()

        } catch (e: Exception) {
            e.printStackTrace()

            return Unit
        } finally {
            //finish
            socket?.let {
                if (it.isConnected) {
                    it.shutdownInput()
                    it.shutdownOutput()
                }
            }
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        }

    }
}