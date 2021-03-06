package com.nullpt.rpc

import com.nullpt.rpc.intercepter.RpcFunctionIntercept
import com.nullpt.rpc.intercepter.RpcResultParseIntercept
import com.nullpt.rpc.intercepter.RpcSecretIntercept
import com.nullpt.rpc.intercepter.RpcSocketIntercept
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

/**
 * rpc net control
 */
class RpcNetLayer {

    companion object {

        private val rpcNetLayerCache = LinkedList<RpcNetLayer>()

        /**
         * better way to create [RpcNetLayer] instance
         */
        fun obtain(): RpcNetLayer {
            synchronized(rpcNetLayerCache) {
                if (rpcNetLayerCache.size > 0) {
                    log { "RpcNetLayer instance cache" }
                    return rpcNetLayerCache.removeAt(0)
                }
            }
            log { "RpcNetLayer instance create" }
            return RpcNetLayer()
        }
    }

    /**
     * try not this function, replace call #[RpcInterfaceProxy.newProxyInstance]
     */
    @Synchronized
    fun request(clazz: Class<*>, method: Method, args: Array<Any>, default: (args: Array<Any>) -> Any = {}): Any {

        return runBlocking(Dispatchers.IO) {

            //net layer
            val intercepts: MutableList<RpcIntercept> = ArrayList()
            intercepts += RpcFunctionIntercept()
            intercepts += RpcResultParseIntercept(default)
            intercepts += RpcSecretIntercept()
            intercepts += RpcSocketIntercept()

            val inStream = RpcStream(clazz, method, args, default)
            val realInterceptorChain = RealInterceptorChain(inStream, intercepts, 0)
            val outStream = realInterceptorChain.proceed(inStream)

            log {
                "result:$outStream"
            }

            val result = outStream.result ?: Unit

            //net finish, instance can use
            rpcNetLayerCache.add(this@RpcNetLayer)

            return@runBlocking result
        }

    }

}