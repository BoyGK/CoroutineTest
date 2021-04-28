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

            while (true) {
                val socket = serverSocket.accept()
                executors.execute(RpcServer(socket))

                if (cancel) {
                    break
                }
            }
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
                val realInterceptorChain = RealInterceptorChain(socket, intercepts, 0)
                val result = realInterceptorChain.proceed(socket)

                log {
                    "rpc function call ${if (result as Boolean) "success" else "error!!!"}"
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