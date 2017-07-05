package com.github.antondym.otus.l5.testframework.test;

import com.github.antondym.otus.l5.testframework.After;
import com.github.antondym.otus.l5.testframework.Before;
import com.github.antondym.otus.l5.testframework.Test;

public class TestClass3 {
    @Before
    public void before() {
        System.out.println("TestClass3.before");
    }

    @After
    public void after() {
        System.out.println("TestClass3.after");
    }

    @Test
    public void test1() {
        System.out.println("TestClass3.test1");
    }

    @Test
    public void test2() {
        System.out.println("TestClass3.test2");
    }
}
