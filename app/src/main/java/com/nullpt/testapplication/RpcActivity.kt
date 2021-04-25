package com.nullpt.testapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.nullpt.rpc.RpcInterfaceProxy
import kotlinx.coroutines.*

class RpcActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rpc)

        val rpcParams1 = findViewById<AppCompatEditText>(R.id.num1)
        val rpcParams2 = findViewById<AppCompatEditText>(R.id.num2)
        val rpcText = findViewById<AppCompatTextView>(R.id.rpc_text)
        val rpcRequest = findViewById<AppCompatButton>(R.id.rpc_request)

        val rpcInterface = RpcInterfaceProxy.newProxyInstance()


        rpcRequest.setOnClickListener {
            launch {
                val result = async(Dispatchers.IO) {
                    rpcInterface.plus(rpcParams1.text.toString().toLong(), rpcParams2.text.toString().toLong())
                }
                rpcText.text = result.await().toString()
            }

        }


    }

}