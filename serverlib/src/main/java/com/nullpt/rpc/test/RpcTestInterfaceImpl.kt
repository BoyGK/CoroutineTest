package com.nullpt.rpc.test

/**
 * test
 */
class RpcTestInterfaceImpl : RpcTestInterface {

    override fun addString(a: String, b: String): String = a + b

    override fun plus(a: Long?, b: Long?): Long = (a ?: 0) + (b ?: 0)
}