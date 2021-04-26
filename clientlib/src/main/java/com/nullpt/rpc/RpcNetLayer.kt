package com.nullpt.rpc

import com.nullpt.rpc.intercepter.RpcFunctionIntercept
import com.nullpt.rpc.intercepter.RpcResultParseIntercept
import com.nullpt.rpc.intercepter.RpcSecretIntercept
import com.nullpt.rpc.intercepter.RpcSocketIntercept
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * rpc net control
 */
class RpcNetLayer {

    @Synchronized
    fun request(clazz: Class<*>, methodName: String, args: Array<Any>, default: (args: Array<Any>) -> Any = {}): Any {

        return runBlocking(Dispatchers.IO) {

            //net layer
            val intercepts: MutableList<RpcIntercept> = ArrayList()
            intercepts += RpcFunctionIntercept(clazz, methodName, args)
            intercepts += RpcResultParseIntercept(default)
            intercepts += RpcSecretIntercept()
            intercepts += RpcSocketIntercept()

            val realInterceptorChain = RealInterceptorChain(Unit, intercepts, 0)
            realInterceptorChain.proceed(Unit)
        }

    }

}