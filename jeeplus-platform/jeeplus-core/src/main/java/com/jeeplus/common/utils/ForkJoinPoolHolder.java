package com.jeeplus.common.utils;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinPoolHolder {

    private static ForkJoinPool forkJoinPool;

    public static ForkJoinPool getForkJoinPool() {
        if (Objects.isNull(forkJoinPool)) {
            forkJoinPool = new ForkJoinPool(20);
        }
        return forkJoinPool;
    }

}
