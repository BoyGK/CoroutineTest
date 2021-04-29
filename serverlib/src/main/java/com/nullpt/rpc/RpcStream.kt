package com.nullpt.rpc

import java.net.Socket

/**
 * rpc intercept stream
 */
data class RpcStream(
        val socket: Socket,
        var rpcObject: RpcObject? = null,
        var result: Any? = null,
        var secretBody: ByteArray? = null,
        var tag: Any? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RpcStream

        if (socket != other.socket) return false
        if (rpcObject != other.rpcObject) return false
        if (result != other.result) return false
        if (secretBody != null) {
            if (other.secretBody == null) return false
            if (!secretBody.contentEquals(other.secretBody)) return false
        } else if (other.secretBody != null) return false
        if (tag != other.tag) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = socket.hashCode()
        result1 = 31 * result1 + (rpcObject?.hashCode() ?: 0)
        result1 = 31 * result1 + (result?.hashCode() ?: 0)
        result1 = 31 * result1 + (secretBody?.contentHashCode() ?: 0)
        result1 = 31 * result1 + (tag?.hashCode() ?: 0)
        return result1
    }
}