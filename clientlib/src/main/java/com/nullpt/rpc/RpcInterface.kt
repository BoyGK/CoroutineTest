package com.nullpt.rpc

interface RpcInterface {

    suspend fun plus(a: Long?, b: Long?): Long

}