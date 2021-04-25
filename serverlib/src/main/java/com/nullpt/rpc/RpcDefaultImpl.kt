package com.nullpt.rpc

class RpcDefaultImpl : RpcInterface {
    override fun plus(a: Long?, b: Long?): Long {
        a ?: return 0
        b ?: return 0
        var count = 0L
        for (i in 0 until (a * b)) {
            count += i
        }
        return count
    }
}