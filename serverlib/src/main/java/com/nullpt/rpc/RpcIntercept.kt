package com.nullpt.rpc

/**
 * rpc net control
 */
interface RpcIntercept {

    fun next(chain: Chain): Any

    interface Chain {

        fun request(): Any

        fun proceed(request: Any): Any

    }

}