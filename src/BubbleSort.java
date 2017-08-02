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
            for (int i = 1; i <= max_count - count; i++) {
                if (param[i - 1] > param[i]) {
                    swap(param, i, i - 1);
                }
            }
            count++;
        }

        return param;
    }

    public int[] sort2(int[] param) {
        int max_count = param.length - 1;//最大下标
        while (max_count > 0) {
            for (int i = 0; i <= max_count - 1; i++) {
                if (param[i] > param[i + 1]) {
                    swap(param, i, i + 1);
                }
            }
            max_count--;
        }

        return param;
    }

/*    public static void main(String[] args) {
       int[] param = new BubbleSort().sort2(new int[]{5, 4, 3, 2, 1, 0, 44, 22, 2, 32, 54, 22, 88, 77, 99, 11, 9, 8, 7, 6, 5, 2, 6, 9, 4, 2, 5, 6, 4, 9});
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < param.length; i++) {
            str.append(param[i]);
            if (i != param.length - 1) {
                str.append(" ");
            }
        }
        str.append("---------------");
        System.out.println(str.toString());
        int[] param2 = new BubbleSort().sort(new int[]{5, 4, 3, 2, 1, 0, 44, 22, 2, 32, 54, 22, 88, 77, 99, 11, 9, 8, 7, 6, 5, 2, 6, 9, 4, 2, 5, 6, 4, 9});
        StringBuilder str2 = new StringBuilder();
        for (int i = 0; i < param2.length; i++) {
            str2.append(param2[i]);
            if (i != param2.length - 1) {
                str2.append(" ");
            }
        }
        str2.append("---------------");
        System.out.println(str2.toString());
    }*/
}

