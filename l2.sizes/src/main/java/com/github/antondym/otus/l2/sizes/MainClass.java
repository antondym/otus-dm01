package com.github.antondym.otus.l2.sizes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

interface ContainerSupplier {
    Object get(int size);
}

public class MainClass {
    private static final int ARRAY_SIZE = 10_000;
    private static final Runtime RUNTIME = Runtime.getRuntime();

    private static final int[] SIZES = {0, 1, 10, 20, 100, 1000};

    static class Measurement {
        long _size;
        Object[] _array;

        Measurement(long size, Object[] array) {
            this._size = size;
            this._array = array;
        }
    }

    private static <T> void fillArray(T[] array, Supplier<T> valueSupplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = valueSupplier.get();
        }
    }

    private static void runForContainer(ContainerSupplier supplier, String description) {
        System.out.println(description + ":");

        String format = "%1$-5s %2$-10s %3$-7s";
        String header = String.format(format, "Size", "Memory", "Per Element");
        System.out.println(header);
        System.out.println(new String(new char[header.length()]).replace('\0', '-'));

        for (final int containerSize : SIZES) {
            Measurement result = measureObject(() -> supplier.get(containerSize));
            System.out.println(String.format(format, containerSize, result._size, (double)result._size / containerSize));
        }

        System.out.println();
    }

    private static void stabilizeMemory() {
        long lastFree;
        long free = RUNTIME.freeMemory();
        do {
            lastFree = free;
            System.gc();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            free = RUNTIME.freeMemory();
        } while (free != lastFree);
    }

    private static void runForObject(Supplier<Object> supplier, String description) {
        Measurement measurement = measureObject(supplier);
        System.out.println(description + ": " + measurement._size);
        System.out.println();
    }

    private static Measurement measureObject(Supplier<Object> supplier) {
        stabilizeMemory();

        long before = RUNTIME.freeMemory();
        Object[] array = new Object[ARRAY_SIZE];
        fillArray(array, supplier);

        stabilizeMemory();
        long after = RUNTIME.freeMemory();
        long size = (before - after) / array.length;

        return new Measurement(size, array);
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println();

        runForObject(() -> new Object(), "new Object()");
        runForObject(() -> new String(), "new String()");
        runForObject(() -> new String(new char[] {}), "new String(new char[] {})");
        runForContainer((size) -> new byte[size], "new byte[]");
        runForContainer((size) -> new short[size], "new short[]");
        runForContainer((size) -> new int [size], "new int[]");
        runForContainer((size) -> new long[size], "new long[]");
        runForContainer((size) -> new boolean[size], "new boolean[]");
        runForContainer((size) -> new char[size], "new char[]");
        runForContainer((size) -> new float[size], "new float[]");
        runForContainer((size) -> new double[size], "new double[]");
        runForContainer((size) -> {
            String[] array = new String[size];
            fillArray(array, String::new);
            return array;
        }, "new String[]");
        runForContainer((size) -> {
            ArrayList<Object> arrayList = new ArrayList<>();
            for (int i = 0; i < size; i++)
                arrayList.add(new Object());
            return arrayList;
        }, "ArrayList");
        runForContainer((size) -> {
            HashSet<Object> hashSet = new HashSet<>();
            for (int i = 0; i < size; i++)
                hashSet.add(new Object());
            return hashSet;
        }, "HashSet");
    }
}
