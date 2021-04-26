package com.nullpt.rpc

/**
 * net config
 */
object Config {
    const val ip = "192.168.42.238"
    const val port = 6789
    const val timeout = 5000
}

/**
 * list<t>+t
 * list<t>+=t
 */
inline operator fun <reified T> ArrayList<T>.plus(t: T): ArrayList<T> {
    return ArrayList<T>().apply {
        addAll(this)
        add(t)
    }
}

/**
 * normal log
 */
fun log(message: () -> String) {
    println("log --> time=${System.currentTimeMillis()}")
    println(message.invoke())
    println("log <-- end\n")
}