/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:16/03/2026
 * Time:15:20
 */


package com.ronem.rupiasecuritylib.util;

import java.util.function.BiFunction;

public class TestClass {
    public void print(String msg) {
        System.out.println(msg);
    }

    public Float calculate(int a, int b, BiFunction<Integer, Integer, Float> operation) {
        return operation.apply(a, b);
    }

}