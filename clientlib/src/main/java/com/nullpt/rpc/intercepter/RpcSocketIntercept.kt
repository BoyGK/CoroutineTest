package com.nullpt.rpc.intercepter

import com.nullpt.rpc.Config
import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.log
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket

/**
 * rpc socket layer
 */
internal class RpcSocketIntercept : RpcIntercept {

    companion object {

        private val socketCache = LinkedHashMap<SocketAddress, Socket>()

        private fun connectedSocket(): Socket {
            synchronized(socketCache) {
                if (socketCache.size > 0) {
                    val address =
                            socketCache.keys.find { Config.ip == it.ip && Config.port == it.port }
                    if (address != null) {
                        val socket = socketCache.remove(address)
                        if (socket != null && socket.isConnected) {
                            //return connected socket
                            log { "use cache socket" }
                            return socket
                        }
                    }
                }
            }
            //create socket and connect
            log { "use new create socket" }
            val socket = Socket()
            socket.connect(InetSocketAddress(Config.ip, Config.port), Config.timeout)
            return socket
        }
    }

    override fun next(chain: RpcIntercept.Chain): Any {
        var socket: Socket? = null
        try {
            //create socket ,and connect
            socket = connectedSocket()

            val rpcRequestObject = chain.request()

            val outputStream = socket.getOutputStream()
            val objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(rpcRequestObject)
            objectOutputStream.flush()

            val inputStream = socket.getInputStream()
            val objectInputStream = ObjectInputStream(inputStream)
            return objectInputStream.readObject()

        } catch (e: Exception) {
            e.printStackTrace()

            return Unit
        } finally {
            //finish
            socket?.let {
                socketCache.put(SocketAddress(Config.ip, Config.port), socket)
            }
        }

    }

    //socket cache
    inner class SocketAddress(val ip: String, val port: Int)
}