package com.github.antondym.otus.l3.containers.test;

import com.github.antondym.otus.l3.containers.MyArrayList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class MyArrayListTest {
    @Test
    public void basicTest() {
        MyArrayList<Object> list = new MyArrayList<>();
        Assert.assertEquals(0, list.size());
        Object o = new Object();
        list.add(o);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(o, list.get(0));
        Assert.assertEquals(o, list.remove(0));
    }

    static class Fibonacci implements IntSupplier {
        private int _p1 = -1, _p2 = 1;

        @Override
        public int getAsInt() {
            _p2 = _p1 + _p2;
            _p1 = _p2 - _p1;
            return _p2;
        }
    }

    @Test
    public void testAddAll() {
        int size = 40;

        List<Integer> myArrayList = new MyArrayList<>();
        IntStream fib1 = IntStream.generate(new Fibonacci());
        Collections.addAll(myArrayList, fib1.limit(size).boxed().toArray(Integer[]::new));

        List<Integer> standardArrayList = new ArrayList<>();
        IntStream fib2 = IntStream.generate(new Fibonacci());
        Collections.addAll(standardArrayList, fib2.limit(size).boxed().toArray(Integer[]::new));

        Assert.assertEquals(size, myArrayList.size());
        Assert.assertEquals(size, standardArrayList.size());

        for (int i = 0; i < size; i++)
            Assert.assertEquals(myArrayList.get(i), standardArrayList.get(i));
    }

    @Test
    public void testCopy() {
        int size = 40;

        List<Integer> myArrayList = new MyArrayList<>(size);
        for (int i = 0; i < size; i++)
            myArrayList.add(0);

        List<Integer> standardArrayList = new ArrayList<>();
        IntStream fib = IntStream.generate(new Fibonacci());
        Collections.addAll(standardArrayList, fib.limit(size).boxed().toArray(Integer[]::new));

        Collections.copy(myArrayList, standardArrayList);

        Assert.assertEquals(size, myArrayList.size());
        Assert.assertEquals(size, standardArrayList.size());

        for (int i = 0; i < size; i++)
            Assert.assertEquals(standardArrayList.get(i), myArrayList.get(i));
    }

    @Test
    public void testSort() {
        List<Integer> myArrayList = new MyArrayList<>();
        Collections.addAll(myArrayList, new Random().ints().limit(100000).boxed().toArray(Integer[]::new));
        Integer min = Collections.min(myArrayList);
        Integer max = Collections.max(myArrayList);

        Collections.sort(myArrayList);

        Assert.assertEquals(min, myArrayList.get(0));
        Integer last = min;
        for (Integer i : myArrayList) {
            Assert.assertTrue(i >= last);
            last = i;
        }
        Assert.assertEquals(max, myArrayList.get(myArrayList.size() - 1));
    }
}
