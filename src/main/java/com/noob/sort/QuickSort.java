package com.noob.sort;

/**
 * 不稳定（判断时，若相等，则出现不稳定状况）
 * 快速排序：关键值排序
 * （1）基本思想：选择一个基准元素,通常选择第一个元素或者最后一个元素,通过一趟扫描，将待排序列分成两部分,一部分比基准元素小,一部分大于等于基准元素,
 * 此时基准元素在其排好序后的正确位置,然后再用同样的方法递归地排序划分的两部分。
 * Created by bear on 2016/3/2.
 */
public class QuickSort extends AbstractSort {
    @Override
    public int[] sort(int[] param) {
        return sort_core(param, 0, param.length - 1);
    }

    /**
     * 左边 < 关键值 <= 右边
     *
     * @param param
     * @param start 开始下标
     * @param end   结束下标
     * @return
     */
    private int[] sort_core(int[] param, int start, int end) {
        int key = param[start];
        int low = start;
        int high = end;
        while (low < high) {
            //比较时不能等于,因为每次符合条件的置换都是与关键值的置换，若等于，指针就会略过关键值，导致置换后的排列不符合 左边 < 关键值 <= 右边。
            while (param[high] > key && low < high) {//从后往前，找到比关键值小的数
                high--;
            }
            while (param[low] < key && low < high) {//从前往后，找到比关键值大的数
                low++;
            }

            if (low >= high) {
                //能够保证low 和high的值不会越过本次循环最小、做大下标值;这样low和high的值在跳出循环时一定是相等的
                //System.out.println(low == high);
                break;
            } else if (param[low] != param[high]) {
                // 如果高位值和低位值相等，则不交换，高位指针减1，低位指针不动，这样下次比较时，比key小的一定在key前面
                swap(param, low, high); //最终关键值会处在中间
            } else {
                //low++; 也可以（第一个判断可以杜绝越界问题）
                high--;//快排的思想是后面的大于或等于关键值
            }

        }

        //迭代快排不需要包括关键值自己。因为左边一定小，右边一定大或等于。
        if (high > start + 1) {
            // 关键值的下标左边有大于1位
            sort_core(param, start, high - 1);
        }
        if (high < end - 1) {
            // 关键值的下标右边有大于1位
            sort_core(param, high + 1, end);
        }
        return param;

    }

}
