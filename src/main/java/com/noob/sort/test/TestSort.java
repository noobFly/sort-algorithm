package com.noob.sort.test;

import com.noob.sort.BubbleSort;
import com.noob.sort.HeapSort;
import com.noob.sort.InsertionSort;
import com.noob.sort.MergeSort;
import com.noob.sort.QuickSort;
import com.noob.sort.RadixSort;
import com.noob.sort.SelectionSort;
import com.noob.sort.ShellSort;

/**
 * Created by bear on 2016/2/29.
 */
public class TestSort {


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
