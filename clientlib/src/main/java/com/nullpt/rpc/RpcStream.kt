package com.nullpt.rpc

import java.lang.reflect.Method

/**
 * rpc intercept stream
 */
internal data class RpcStream(
        val clazz: Class<*>,
        val method: Method,
        val args: Array<Any>,
        val default: (args: Array<Any>) -> Any,
        var rpcObject: RpcObject? = null,
        var secretBody: ByteArray? = null,
        var result: Any? = null,
        var tag: Any? = null
) {

    override fun toString(): String {
        return "class:${clazz.name},method:${method.name},args:${args.contentToString()},result:${result.toString()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RpcStream

        if (clazz != other.clazz) return false
        if (method != other.method) return false
        if (!args.contentEquals(other.args)) return false
        if (default != other.default) return false
        if (rpcObject != other.rpcObject) return false
        if (secretBody != null) {
            if (other.secretBody == null) return false
            if (!secretBody.contentEquals(other.secretBody)) return false
        } else if (other.secretBody != null) return false
        if (result != other.result) return false
        if (tag != other.tag) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = clazz.hashCode()
        result1 = 31 * result1 + method.hashCode()
        result1 = 31 * result1 + args.contentHashCode()
        result1 = 31 * result1 + default.hashCode()
        result1 = 31 * result1 + (rpcObject?.hashCode() ?: 0)
        result1 = 31 * result1 + (secretBody?.contentHashCode() ?: 0)
        result1 = 31 * result1 + (result?.hashCode() ?: 0)
        result1 = 31 * result1 + (tag?.hashCode() ?: 0)
        return result1
    }

}