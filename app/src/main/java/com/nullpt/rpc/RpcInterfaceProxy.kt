package com.nullpt.rpc

import java.lang.RuntimeException
import java.lang.reflect.Proxy

object RpcInterfaceProxy {

    private val rpcServer by lazy { RpcNetLayer() }

    fun newProxyInstance(): RpcInterface {
        return Proxy.newProxyInstance(RpcInterface::class.java.classLoader,
                arrayOf(RpcInterface::class.java)) { _, method, args ->
            rpcServer.request(RpcInterface::class.java, method.name, args) {
                //default,may be not
                when (method.name) {
                    "plus" -> {
                        plus(args[0] as Long?, args[1] as Long?)
                    }
                    else -> {
                        throw RuntimeException("function error")
                    }
                }
            }
        } as RpcInterface
    }

    private fun plus(a: Long?, b: Long?): Long {
        a ?: return 0
        b ?: return 0
        return a + b
    }

}