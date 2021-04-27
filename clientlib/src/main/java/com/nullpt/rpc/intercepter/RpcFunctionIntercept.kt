package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import java.lang.reflect.Method

/**
 * rpc function
 * build the data passed
 */
internal class RpcFunctionIntercept(
        private val clazz: Class<*>,
        private val method: Method,
        private val args: Array<Any>
) : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        val methodName = method.name
        //coroutine make other params in the last...
        val realArgs = args.copyOfRange(0, args.size - 1)
        val rpcObject = RpcObject(clazz, methodName, realArgs)
        return chain.proceed(rpcObject)
    }

}