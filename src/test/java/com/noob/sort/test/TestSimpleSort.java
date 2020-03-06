package com.noob.sort.test;

import com.noob.sort.simple.BubbleSort;
import com.noob.sort.simple.HeapSort;
import com.noob.sort.simple.InsertionSort;
import com.noob.sort.simple.MergeSort;
import com.noob.sort.simple.QuickSort;
import com.noob.sort.simple.RadixSort;
import com.noob.sort.simple.SelectionSort;
import com.noob.sort.simple.ShellSort;

/**
 * Created by bear on 2016/2/29.
 */
public class TestSimpleSort {


    public static void main(String[] args) {

        new BubbleSort().print();
        new InsertionSort().print();
        new ShellSort().print();
        new SelectionSort().print();
        new MergeSort().print();
        new QuickSort().print();
        new HeapSort().print();
        new RadixSort().print();


    }
}
