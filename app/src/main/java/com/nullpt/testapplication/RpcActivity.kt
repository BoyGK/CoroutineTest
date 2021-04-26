package com.nullpt.testapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.nullpt.rpc.RpcInterfaceProxy
import com.nullpt.rpc.tets.RpcTestInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

        val rpcInterface = RpcInterfaceProxy.newProxyInstance(RpcTestInterface::class.java)

        rpcRequest.setOnClickListener {
            launch {
                val result1 = async(Dispatchers.IO) {
                    rpcInterface.plus(rpcParams1.text.toString().toLong(), rpcParams2.text.toString().toLong())
                }
                val result2 = async(Dispatchers.IO) {
                    rpcInterface.addString("123qwe", "123456")
                }
                rpcText.text = result1.await().toString() + "\n" + result2.await()
            }

        }


    }

}