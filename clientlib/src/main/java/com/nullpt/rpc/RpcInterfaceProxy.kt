package com.nullpt.rpc

import java.lang.RuntimeException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object RpcInterfaceProxy {

    val rpcServer by lazy { RpcNetLayer() }

    inline fun <reified T : RpcInterface> newProxyInstance(
            clazz: Class<T>, crossinline default: (method: Method, args: Array<Any>) -> Any = { _, _ -> }): T {
        if (clazz !is RpcInterface) {
            throw RuntimeException("clazz need imp RpcInterface")
        }
        return Proxy.newProxyInstance(
                clazz.classLoader, arrayOf(RpcInterface::class.java)) { _, method, args ->
            rpcServer.request(clazz, method.name, args) {
                default(method, args)
            }
        } as T
    }

}