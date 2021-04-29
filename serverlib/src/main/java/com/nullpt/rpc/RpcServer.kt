package com.nullpt.rpc

import com.nullpt.rpc.intercepter.RpcDispatchIntercept
import com.nullpt.rpc.intercepter.RpcSecretIntercept
import com.nullpt.rpc.intercepter.RpcSocketIntercept
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/**
 * rpc server
 */
class RpcServer(private val socket: Socket) : Runnable {

    companion object {
        @Volatile
        private var cancel = false

        fun receive() {

            val executors = Executors.newCachedThreadPool()

            //create server
            val serverSocket = ServerSocket(Config.port)
            do {
                val socket = serverSocket.accept()
                executors.execute(RpcServer(socket))
            } while (!cancel)

        }

        /**
         * after next task
         */
        fun stop() {
            cancel = true
        }
    }

    override fun run() {

        //net layer
        val intercepts: MutableList<RpcIntercept> = ArrayList()
        intercepts += RpcSocketIntercept()
        intercepts += RpcSecretIntercept()
        intercepts += RpcDispatchIntercept()

        while (true) {
            try {
                //socket reuse
                val inStream = RpcStream(socket)
                val realInterceptorChain = RealInterceptorChain(inStream, intercepts, 0)
                val outStream = realInterceptorChain.proceed(inStream)

                log {
                    "rpc function call ${if (outStream.result as Boolean) "success" else "error!!!"}"
                }
            } catch (e: Exception) {
                if (!socket.isInputShutdown) {
                    socket.shutdownInput()
                }
                if (!socket.isOutputShutdown) {
                    socket.shutdownOutput()
                }
                if (socket.isConnected) {
                    socket.close()
                }
                log {
                    e.message ?: "socket close!!"
                }
                break
            }
        }
    }
}