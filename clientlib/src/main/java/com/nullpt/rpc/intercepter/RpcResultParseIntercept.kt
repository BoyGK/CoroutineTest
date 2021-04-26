package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject

/**
 * rpc result call
 * make sure has result
 */
class RpcResultParseIntercept(private val default: (args: Array<Any>) -> Any) : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        val rpcObject = chain.request() as RpcObject
        return when (val result = chain.proceed(rpcObject)) {
            is Unit -> {
                default.invoke(rpcObject.args)
            }
            else -> {
                result
            }
        }

    }
}