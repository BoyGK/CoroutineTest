package com.nullpt.rpc

import java.lang.reflect.Method

/**
 * rpc intercept stream
 */
data class RpcStream(
        val clazz: Class<*>,
        val method: Method,
        val args: Array<Any>,
        val default: (args: Array<Any>) -> Any,
        var rpcObject: RpcObject? = null,
        var secretRpcObject: Any? = null,
        var result: Any? = null,
        var tag: Any? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RpcStream

        if (clazz != other.clazz) return false
        if (method != other.method) return false
        if (!args.contentEquals(other.args)) return false
        if (default != other.default) return false
        if (rpcObject != other.rpcObject) return false
        if (secretRpcObject != other.secretRpcObject) return false
        if (tag != other.tag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clazz.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + args.contentHashCode()
        result = 31 * result + default.hashCode()
        result = 31 * result + (rpcObject?.hashCode() ?: 0)
        result = 31 * result + (secretRpcObject?.hashCode() ?: 0)
        result = 31 * result + (tag?.hashCode() ?: 0)
        return result
    }

}