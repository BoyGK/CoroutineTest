package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.RpcStream

/**
 * rpc function
 * build the data passed
 */
internal class RpcFunctionIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        val inStream = chain.request()
        //coroutine make other params in the last...
        val realArgs = inStream.args.copyOfRange(0, inStream.args.size - 1)
        inStream.rpcObject = RpcObject(inStream.clazz, inStream.method.name, realArgs)
        return chain.proceed(inStream)
    }

}