package com.nullpt.rpc

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
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
                "test" -> {
                    try {
                        val socket = Socket("localhost", 6789)
                        val os = socket.getOutputStream()
                        val iss = socket.getInputStream()
                        val objectOutputStream = ObjectOutputStream(os)

                        val rpcObject = RpcObject(RpcInterface::class.java, "plus", arrayOf(123L, 456L))
                        objectOutputStream.writeObject(testEncrypt(rpcObject))
                        os.flush()

                        val result = ObjectInputStream(iss).readObject()
                        log {
                            "rpc result:${testDecrypt(result)}"
                        }

                        //finish
                        socket.shutdownInput()
                        socket.shutdownOutput()
                        iss.close()
                        os.close()
                        socket.close()
                    } catch (ignore: Exception) {
                        executors.shutdown()
                        ignore.printStackTrace()
                        return@execute
                    }
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
    val byteArrayInputStream = ByteArrayInputStream(rpcRequestObject as ByteArray)
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