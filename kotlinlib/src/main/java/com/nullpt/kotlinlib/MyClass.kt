package com.nullpt.kotlinlib

fun main() {

    println(3 addto 9)

}

infix fun Int.addto(num: Int): String {
    var count = num - this
    var result = ""
    while (count-- > 0) {
        result += "${num - count}"
    }
    return result
}


open class B(val index: Int) {

    operator fun plus(target: B): B {
        return B(index + target.index)
    }
}

open class A {

}

interface C {

}

class Normal<T : Normal<T>> {


}