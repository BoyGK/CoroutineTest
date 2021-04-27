package com.nullpt.rpc

/**
 * rpc net control
 */
internal class RealInterceptorChain(
        private val request: Any,
        private val intercepts: List<RpcIntercept>,
        private val index: Int
) : RpcIntercept.Chain {

    override fun request(): Any {
        return request
    }

    override fun proceed(request: Any): Any {
        val intercept: RpcIntercept = intercepts[index]
        val next = RealInterceptorChain(request, intercepts, index + 1)
        return intercept.next(next)
    }

}