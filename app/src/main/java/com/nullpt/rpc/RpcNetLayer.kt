package com.nullpt.rpc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class RpcNetLayer {

    companion object {
        private const val temp_ip = "192.168.42.238"
        private const val temp_port = 6789
        private const val temp_timeout = 5000
    }

    @Synchronized
    fun request(clazz: Class<*>, methodName: String, args: Array<Any>, default: (args: Array<Any>) -> Any = {}): Any {

        return runBlocking(Dispatchers.IO) {
            var socket: Socket? = null
            var os: OutputStream? = null
            var iss: InputStream? = null
            var objectOutputStream: ObjectOutputStream? = null
            try {
                socket = Socket()
                socket.connect(InetSocketAddress(temp_ip, temp_port), temp_timeout)
                os = socket.getOutputStream()
                iss = socket.getInputStream()
                objectOutputStream = ObjectOutputStream(os)

                val rpcObject = RpcObject(clazz, methodName, args)
                objectOutputStream.writeObject(rpcObject)
                os.flush()

                ObjectInputStream(iss).readObject()
            } catch (e: Exception) {
                e.printStackTrace()

                default.invoke(args)
            } finally {
                //finish
                socket?.let {
                    if (it.isConnected) {
                        it.shutdownInput()
                        it.shutdownOutput()
                    }
                }
                iss?.close()
                os?.close()
                objectOutputStream?.close()
                socket?.close()
            }
        }

    }

}