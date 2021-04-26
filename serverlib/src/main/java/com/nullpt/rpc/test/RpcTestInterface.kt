package com.nullpt.rpc.test

import com.nullpt.rpc.RpcInterface

/**
 * test
 */
interface RpcTestInterface : RpcInterface {

    fun addString(a: String, b: String): String
}