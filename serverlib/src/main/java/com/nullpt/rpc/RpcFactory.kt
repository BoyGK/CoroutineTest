package com.nullpt.rpc

import com.nullpt.rpc.test.RpcTestInterfaceImpl

/**
 * rpc instance factory
 */
object RpcFactory {

    private val mRpcMap = mapOf(
            "com.nullpt.rpc.RpcInterface" to RpcDefaultImpl::class.java,
            "com.nullpt.rpc.test.RpcTestInterface" to RpcTestInterfaceImpl::class.java
    )

    private val mRpcInstanceCache = mutableMapOf<String, Any>()

    fun getInstance(interfaze: String): Any {

        if (!mRpcInstanceCache.containsKey(interfaze)) {
            synchronized(mRpcInstanceCache) {
                if (!mRpcInstanceCache.containsKey(interfaze)) {
                    val instance =
                            Class.forName((mRpcMap[interfaze]
                                    ?: error("rpc interface is not exist")).name).newInstance()
                    mRpcInstanceCache[interfaze] = instance
                }
            }
        }

        return mRpcInstanceCache[interfaze]!!
    }

}