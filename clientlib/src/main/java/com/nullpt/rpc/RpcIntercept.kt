package com.nullpt.rpc

/**
 * rpc net control
 */
internal interface RpcIntercept {

    fun next(chain: Chain): RpcStream

    interface Chain {

        fun request(): RpcStream

        fun proceed(request: RpcStream): RpcStream

    }

}