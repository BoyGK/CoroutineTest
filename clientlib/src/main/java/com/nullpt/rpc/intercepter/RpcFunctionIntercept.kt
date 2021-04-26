package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject

/**
 * rpc function
 * build the data passed
 */
class RpcFunctionIntercept(
        private val clazz: Class<*>,
        private val methodName:
        String, private val args: Array<Any>
) : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        val rpcObject = RpcObject(clazz, methodName, args)
        return chain.proceed(rpcObject)
    }

}