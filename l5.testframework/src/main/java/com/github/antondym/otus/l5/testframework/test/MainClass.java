package com.github.antondym.otus.l5.testframework.test;

import com.github.antondym.otus.l5.testframework.runner.Core;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("Class run");
        System.out.println("=========");
        Core.run(TestClass1.class, TestClass2.class);
        System.out.println();

        System.out.println("Package run");
        System.out.println("===========");
        Core.run(TestClass1.class.getPackage().getName());
        System.out.println();
    }
}
