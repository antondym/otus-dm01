package com.github.antondym.otus.l4.jmx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.LongStream;

public class MainClass {
    private static Supplier<LongStream> LONGS = () -> LongStream.iterate(0, operand -> operand + 1);
    private static int STEP = 1000;

    public static void main(String[] args) {
        new Agent();

        int supplied = 0;
        List<Long> list = new ArrayList<>();

        while (true) {
            Collections.addAll(list, LONGS.get().boxed().skip(supplied).limit(STEP).toArray(Long[]::new));
            supplied += STEP;

            // Remove every second of the new objects
            int desiredSize = list.size() - STEP / 2;
            for (int index = list.size() - 1; list.size() > desiredSize; index -= 2)
                list.remove(index);

//            System.out.println(list.size() + " objects");
        }
    }
}
