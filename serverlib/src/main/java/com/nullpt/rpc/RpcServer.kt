package com.nullpt.rpc

import com.nullpt.rpc.intercepter.RpcDispatchIntercept
import com.nullpt.rpc.intercepter.RpcSecretIntercept
import com.nullpt.rpc.intercepter.RpcSocketIntercept
import java.net.ServerSocket


class RpcServer {

    @Volatile
    private var cancel = false

    fun receive() {

        //create server
        val serverSocket = ServerSocket(6789)

        //net layer
        val intercepts: MutableList<RpcIntercept> = ArrayList()
        intercepts += RpcSocketIntercept()
        intercepts += RpcSecretIntercept()
        intercepts += RpcDispatchIntercept()

        val realInterceptorChain = RealInterceptorChain(serverSocket, intercepts, 0)

        while (true) {
            val result = realInterceptorChain.proceed(serverSocket)

            log {
                "rpc function call ${if (result as Boolean) "success" else "error!!!"}"
            }

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