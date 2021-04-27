package com.nullpt.rpc

/**
 * function should suspend
 */
interface RpcInterface {

    suspend fun plus(a: Long?, b: Long?): Long

}