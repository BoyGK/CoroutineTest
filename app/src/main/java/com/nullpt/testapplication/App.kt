package com.nullpt.testapplication

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.nullpt.testapplication.widget.log
import com.nullpt.testapplication.widget.registerActivityLifecycleCallbacks
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks by noOperation() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                log {
                    "activity created, activity=${activity.componentName}"
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                log {
                    "activity destroy, activity=${activity.componentName}"
                }
            }
        })

        registerActivityLifecycleCallbacks(onActivityCreated = { activity, _ ->
            log {
                "activity created, activity=${activity.componentName}"
            }
        })

        ARouter.init(this)


    }

    private inline fun <reified T> noOperation(): T {

        val javaClass = T::class.java
        val noOperationHandler = InvocationHandler { _, method, _ ->
            //do nothing
            log { "call method = ${method.name}" }
        }
        return Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass), noOperationHandler) as T
    }


}