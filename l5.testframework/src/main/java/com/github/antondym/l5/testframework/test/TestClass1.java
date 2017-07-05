package com.github.antondym.l5.testframework.test;

import com.github.antondym.l5.testframework.After;
import com.github.antondym.l5.testframework.Before;
import com.github.antondym.l5.testframework.Test;

public class TestClass1 {
    @Before
    public void before() {
        System.out.println("TestClass1.before");
    }

    @After
    public void after() {
        System.out.println("TestClass1.after");
    }

    @Test
    public void test1() {
        System.out.println("TestClass1.test1");
    }

    @Test
    public void test2() {
        System.out.println("TestClass1.test2");
    }
}
