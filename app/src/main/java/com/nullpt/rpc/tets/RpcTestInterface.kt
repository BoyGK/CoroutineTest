package com.nullpt.rpc.tets

import com.nullpt.rpc.RpcInterface

/**
 * test
 */
interface RpcTestInterface : RpcInterface {

    fun addString(a: String, b: String): String
}