package com.nullpt.testapplication

import android.app.Activity
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.BlurTransformation
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.nullpt.testapplication.widget.log
import kotlinx.coroutines.*
import java.lang.reflect.Method
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis

@Route(path = "/test/CoilActivity")
class CoilTestActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    val a by A()

    val image by lazy { findViewById<AppCompatImageView>(R.id.image) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coil_test)

        try {
            val queue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Looper.getMainLooper().queue
            } else {
                TODO("VERSION.SDK_INT < M")
                //reflect
            }
            val method = MessageQueue::class.java.getDeclaredMethod("postSyncBarrier", Int.javaClass)
            method.isAccessible = true;
            val token = method.invoke(queue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //ARouter.getInstance().build("/test/CoilActivity").navigation()

//        val clazz = Class.forName("com.nullpt.kotlinlib.MyClass")
//        log {
//            clazz.toString()
//        }


//        Handler().sendMessageAtFrontOfQueue(Message.obtain())
//
//        runBlocking {
//
//            val time = measureTimeMillis {
//                val a = async { dosome1() }
//                val b = async { dosome2() }
//                Log.i("BGQ", "a+b=${a.await() + b.await()}")
//            }
//            Log.i("BGQ", "time=$time")
//
//        }


//        val interceptor = object : Interceptor by A() {
//
//        }
//
//        GlobalScope.launch(Dispatchers.Main) {
//            loadImage()
//        }
//
//
//        val job = launch {
//            delay(100)
//
//
//            measureTimeMillis {
//
//            }
//
//        }
//
//
//
//
//
//        launch {
//            job.join()
//
//            val a = async { 1 + 2 }
//            val b = async { 1 + 2 }
//            a.start()
//            b.start()
//
//            withContext(Dispatchers.Unconfined) {
//
//            }
//
//            print(a.await() + b.await())
//        }
//
//        runBlocking {
//
//        }

    }

    suspend fun dosome1(): Int {
        delay(1200)
        return 11
    }

    suspend fun dosome2(): Int {
        delay(1300)
        return 13
    }


    suspend fun loadImage() {
        val dis = image.load(R.drawable.ic_launcher_background) {
            transformations(BlurTransformation(this@CoilTestActivity, 15f, 3f))
        }

        dis.await()
        dis.dispose()
        ///image.drawable.
    }

    interface Interceptor {
        fun getName()
    }

    class A : Interceptor {
        override fun getName() {

        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>) {}

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {}

    }

    private fun <T> fibv(isInitialize: () -> T): T {
        return isInitialize.invoke()
    }

    private fun <T> fib(isInitialize: (id: Int) -> T): Lazy<T> = FindViewById(isInitialize)

    inner class FindViewById<out T>(val isInitialize: (id: Int) -> T) : Lazy<T> {

        private var isInitialized = false

        override val value: T
            get() = isInitialize(0)

        override fun isInitialized(): Boolean {

            return isInitialized
        }

    }

    inner class FindView<T> {
        fun getValue(thisRef: Any?, property: KProperty<*>): T? = null
    }

    class Deleget : ReadWriteProperty<Any, String> {
        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            TODO("Not yet implemented")
        }

        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            TODO("Not yet implemented")
        }

    }

    // 新建 Extras 类作为被委托类
    class Extras<out T>(private val key: String, private val default: T) {
        // 重载取值操作符
        operator fun getValue(thisRef: Any, kProperty: KProperty<*>): T? =
                when (thisRef) {
                    // 获取传递给 Activity 的参数
                    is Activity -> {
                        thisRef.intent?.extras?.get(key) as? T ?: default
                    }
                    // 获取传递给 Fragment 的参数
                    is Fragment -> {
                        thisRef.arguments?.get(key) as? T ?: default
                    }
                    else -> default
                }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    class ABC<T> {

        val a: List<T> = ArrayList()

    }


}