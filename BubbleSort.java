/**
 * 稳定
 * 冒泡排序： 相邻的两个值比较，将最大/小的值逐步换至队尾/首
 * Created by bear on 2016/3/2.
 */
public class BubbleSort extends AbstractSort {

    @Override
    public int[] sort(int[] param) {
        int count = 0; //已经排序过的次数
        int max_count = param.length - 1;//最大的排序次数
        while (count <= max_count) {
            for (int i = 1; i < max_count - count; i++) {
                if (param[i - 1] > param[i]) {
                    swap(param, i, i - 1);
                }
            }
            count++;
        }

        return param;
    }
}

