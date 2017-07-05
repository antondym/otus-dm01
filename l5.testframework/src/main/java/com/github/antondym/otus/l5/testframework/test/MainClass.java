package com.github.antondym.otus.l5.testframework.test;

import com.github.antondym.otus.l5.testframework.TestFramework;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("Class run");
        System.out.println("=========");
        TestFramework.run(TestClass1.class, TestClass2.class);
        System.out.println();

        System.out.println("Package run");
        System.out.println("===========");
        TestFramework.run(TestClass1.class.getPackage().getName());
        System.out.println();
    }
}
