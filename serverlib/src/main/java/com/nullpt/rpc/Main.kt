package com.nullpt.rpc

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.Executors


fun main() {

    val executors = Executors.newCachedThreadPool()

    //start server
    executors.execute { RpcServer.receive() }

    //start test client
    executors.execute {
        val scanner = Scanner(System.`in`)
        while (scanner.hasNext()) {
            when (scanner.next()) {
                "stop" -> {
                    RpcServer.stop()
                }
            }
        }
    }

}

/**
 * assume decrypt
 */
private fun testDecrypt(rpcRequestObject: Any): Any {
    if (rpcRequestObject !is ByteArray) {
        log {
            "decrypt error!"
        }
        return Unit
    }
    val byteArrayInputStream = ByteArrayInputStream(rpcRequestObject)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    return objectInputStream.readObject()
}

/**
 * assume encrypt
 */
private fun testEncrypt(result: Any): Any {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(result)
    objectOutputStream.close()
    return byteArrayOutputStream.toByteArray()
}