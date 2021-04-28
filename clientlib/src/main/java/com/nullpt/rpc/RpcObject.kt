package com.nullpt.rpc

import java.io.Serializable

/**
 * rpc params object
 */
class RpcObject(
        val clazz: Class<*>,
        val methodName: String,
        val args: Array<Any>
) : Serializable
