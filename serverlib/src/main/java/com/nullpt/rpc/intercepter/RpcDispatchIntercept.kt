package com.nullpt.rpc.intercepter

import com.nullpt.rpc.*
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * rpc dispatch function call
 */
class RpcDispatchIntercept : RpcIntercept {

    override fun next(chain: RpcIntercept.Chain): RpcStream {
        val inStream = chain.request()
        val rpcObject = inStream.rpcObject ?: return inStream
        val logInfo: (info: String) -> Unit = {
            log { it }
        }
        inStream.result = dispatchRpc(rpcObject, logInfo)
        return inStream
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