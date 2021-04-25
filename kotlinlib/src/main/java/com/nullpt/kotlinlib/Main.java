package com.nullpt.kotlinlib;

import java.util.BitSet;

public class Main {

    public static void main(String[] args) {
        Normal normal = new Main().new Normal();
        System.out.println(normal.t);

    }


    abstract class A<T extends A<T>> {

        public T t;
    }

    class Normal extends A {

    }

}
