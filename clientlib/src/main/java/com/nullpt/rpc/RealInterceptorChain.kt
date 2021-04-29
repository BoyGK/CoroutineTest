package com.nullpt.rpc

/**
 * rpc net control
 */
internal class RealInterceptorChain(
        private val request: RpcStream,
        private val intercepts: List<RpcIntercept>,
        private val index: Int
) : RpcIntercept.Chain {

    override fun request(): RpcStream {
        return request
    }

    override fun proceed(request: RpcStream): RpcStream {
        val intercept: RpcIntercept = intercepts[index]
        val next = RealInterceptorChain(request, intercepts, index + 1)
        return intercept.next(next)
    }

}