package com.nullpt.rpcserver

import com.nullpt.rpc.RpcInterface
import com.nullpt.rpc.RpcObject
import com.nullpt.rpc.RpcServer
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.net.Socket
import java.util.*
import java.util.concurrent.Executors


fun main() {

    val rpc = RpcServer()
    val executors = Executors.newFixedThreadPool(2)

    executors.execute {
        rpc.receive()
    }

    executors.execute {
        val scanner = Scanner(System.`in`)
        while (scanner.hasNext()) {
            when (scanner.next()) {
                "stop" -> {
                    rpc.stop()
                }
                "test" -> {
                    try {
                        val socket = Socket("192.168.42.238", 6789)
                        val os = socket.getOutputStream()
                        val iss = socket.getInputStream()
                        val objectOutputStream = ObjectOutputStream(os)

                        val rpcObject = RpcObject(RpcInterface::class.java, "plus", arrayOf(123L, 456L))
                        objectOutputStream.writeObject(rpcObject)
                        os.flush()

                        val result = ObjectInputStream(iss).readObject()
                        println("rpc result:${result}")

                        //finish
                        socket.shutdownInput()
                        socket.shutdownOutput()
                        iss.close()
                        os.close()
                        socket.close()
                    } catch (ignore: Exception) {
                        executors.shutdown()
                        return@execute
                    }
                }
            }
        }
    }
}