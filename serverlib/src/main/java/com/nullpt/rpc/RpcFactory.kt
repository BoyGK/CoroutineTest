package com.nullpt.rpc

object RpcFactory {

    private val mMap = mapOf(
        "com.nullpt.rpc.RpcInterface" to RpcDefaultImpl::class.java
    )

    fun getInstance(interfaze: String): Any {
        return Class.forName(mMap[interfaze]!!.name).newInstance()
    }

}