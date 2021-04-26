package com.nullpt.rpc.intercepter

import com.nullpt.rpc.RpcFactory
import com.nullpt.rpc.RpcIntercept
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.log
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * rpc dispatch function call
 */
class RpcDispatchIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): Any {
        val rpcObject = chain.request() as RpcObject
        val logInfo: (info: String) -> Unit = {
            log { it }
        }
        return dispatchRpc(rpcObject, logInfo)
    }

    private fun dispatchRpc(rpcObject: RpcObject, callback: (info: String) -> Unit = {}): Any {

        var result: Any
        try {
            result = doRpcFunction(rpcObject)
        } catch (e: Exception) {
            result = Unit
            log { e.message ?: "rpc function call error , result error..." }
        }

        callback.invoke(
                """
                request->
                interface:  ${rpcObject.clazz.name},
                method:     ${rpcObject.methodName},
                args:       ${rpcObject.args.contentToString()},
                args length:${rpcObject.args.size},
                
                response->
                result:     $result
                end--
        """.trimIndent()
        )

        return result

    }

    @Throws(Exception::class)
    private fun doRpcFunction(rpcObject: RpcObject): Any {
        val targetObject = RpcFactory.getInstance(rpcObject.clazz.name)
        val argsClass = Array<Class<*>>(rpcObject.args.size) { rpcObject.args[it].javaClass }
        val targetMethod = targetObject.javaClass.getMethod(rpcObject.methodName, *argsClass)
        return targetMethod.invoke(targetObject, *rpcObject.args)
    }

}