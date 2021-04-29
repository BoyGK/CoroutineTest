package com.nullpt.rpc

/**
 * rpc net control
 */
interface RpcIntercept {

    fun next(chain: Chain): RpcStream

    interface Chain {

        fun request(): RpcStream

        fun proceed(request: RpcStream): RpcStream

    }

}