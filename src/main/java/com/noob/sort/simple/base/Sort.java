package com.noob.sort.simple.base;

/**
 * Created by bear on 2016/2/29.
 */
public interface Sort {
    int[] sort(int[] param);

    void print();

    void swap(int[] param, int source, int target);
}
