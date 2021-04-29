package com.nullpt.testapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.nullpt.rpc.RpcInterfaceProxy
import com.nullpt.rpc.test.RpcTestInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.lang.reflect.Method

/**
 * @author BGQ
 * rpc test
 */
class RpcActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rpc)

        val rpcParams1 = findViewById<AppCompatEditText>(R.id.num1)
        val rpcParams2 = findViewById<AppCompatEditText>(R.id.num2)
        val rpcText = findViewById<AppCompatTextView>(R.id.rpc_text)
        val rpcRequest = findViewById<AppCompatButton>(R.id.rpc_request)

        val rpcInterface =
                RpcInterfaceProxy.newProxyInstance(RpcTestInterface::class.java, ::defaultFunction)

        //每个方法阻塞5s,测试一共阻塞5s?
        rpcRequest.setOnClickListener {
            launch {
                val result1 = async(Dispatchers.IO) {
                    val a = if (rpcParams1.text.isNullOrEmpty()) 0 else rpcParams1.text.toString().toLong()
                    val b = if (rpcParams2.text.isNullOrEmpty()) 0 else rpcParams2.text.toString().toLong()
                    rpcInterface.plus(a, b)
                }
                val result2 = async(Dispatchers.IO) {
                    rpcInterface.addString("baiguoqing", "-gogogo")
                }
                rpcText.text = result1.await().toString() + "\n" + result2.await()
            }

        }


    }

    private fun defaultFunction(method: Method, args: Array<Any>): Any {
        return when (method.name) {
            "plus" -> {
                99999
            }
            "addString" -> {
                "default"
            }
            else -> {
                throw IllegalArgumentException(method.name)
            }
        }
    }

}