package com.nullpt.rpc

import java.lang.reflect.Method
import java.lang.reflect.Proxy

object RpcInterfaceProxy {

    inline fun <reified T : RpcInterface> newProxyInstance(
            clazz: Class<T>, crossinline default: (method: Method, args: Array<Any>) -> Any = { _, _ -> }): T {
        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { _, method, args ->
            RpcNetLayer.obtain().request(clazz, method, args) {
                default(method, args)
            }
        } as T
    }

}