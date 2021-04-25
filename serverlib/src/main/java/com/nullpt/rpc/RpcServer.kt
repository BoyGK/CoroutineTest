package com.nullpt.rpc

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.ServerSocket

class RpcServer {

    @Volatile
    private var cancel = false

    fun receive() {

        val serverSocket = ServerSocket(6789)
        while (true) {
            val socket = serverSocket.accept()
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()

            doSomething(inputStream, outputStream) {
                //log
                println(it)
            }

            if (cancel) {
                //finish
                socket.shutdownInput()
                socket.shutdownOutput()
                outputStream.close()
                inputStream.close()
                socket.close()
                break
            }
        }
    }

    private fun doSomething(
        inputStream: InputStream,
        outputStream: OutputStream,
        callback: (info: String) -> Unit = {}
    ) {
        val request = ObjectInputStream(inputStream)
        val rpcObject = request.readObject() as RpcObject
        val result = dispatchRpc(rpcObject)

        callback.invoke(
            """
                request->
                interface:  ${rpcObject.clazz.name},
                method:     ${rpcObject.methodName},
                args:       ${rpcObject.args.contentToString()},
                args length:${rpcObject.args.size},
                
                response->
                result:     $result
                
                end--
                
        """.trimIndent()
        )

        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(result)
        objectOutputStream.flush()

    }

    private fun dispatchRpc(rpcObject: RpcObject): Any {
        val targetObject = RpcFactory.getInstance(rpcObject.clazz.name)
        val argsClass = Array<Class<*>>(rpcObject.args.size) { rpcObject.args[it].javaClass }
        val targetMethod = targetObject.javaClass.getMethod(rpcObject.methodName, *argsClass)
        return targetMethod.invoke(targetObject, *rpcObject.args)
    }

    /**
     * after next task
     */
    fun stop() {
        cancel = true
    }
}