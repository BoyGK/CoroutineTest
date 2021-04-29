package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcStream

/**
 * rpc result call
 * make sure has result
 */
internal class RpcResultParseIntercept(private val default: (args: Array<Any>) -> Any) : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        val outStream = chain.proceed(chain.request())
        if (outStream.result == null || outStream.result == Unit) {
            outStream.result = default.invoke(outStream.args)
        }
        return outStream
    }
}