/**
 * 按个位、十位、百位...排序  -----> 当前版本对负值无效
 * <p/>
 * 稳定排序
 * 基数排序
 * Created by bear on 2016/3/6.
 */
public class RadixSort extends AbstractSort {
    private int[] radix = new int[]{1, 1, 10, 100, 1000};
    private int max_index = 10;

    public int[] sort(int[] param) {
        int end = param.length - 1;
        for (int position = 0; position < 3; position++) {
            sort_core(param, end, position);
        }
        return param;
    }

    /**
     * 按指定位上的数值排序
     *
     * @param param
     * @param end_index
     * @param position
     */
    private void sort_core(int[] param, int end_index, int position) {
        int[] count = new int[max_index];//记录每个桶统计个数
        int[] store = new int[end_index + 1]; // 排序后的数组容器

        // 置空各个桶的数据统计
        for (int i = 0; i < max_index; i++) {
            count[i] = 0;
        }

        // 统计各个桶将要装入的数据个数
        for (int i = 0; i <= end_index; i++) {
            count[getDigit(param[i], position)]++;
        }

        for (int i = 1; i < max_index; i++) {
            count[i] = count[i] + count[i - 1]; // count[i]表示第i个桶的右边界索引(相对于整个store的索引值)
        }
        // 这里要从右向左扫描，保证排序稳定性
        for (int i = end_index; i >= 0; i--) {
            int digit = getDigit(param[i], position);
            store[count[digit] - 1] = param[i]; //放入对应的桶中，count[j]-1是第j个桶的右边界索引
            count[digit]--; // 对应桶的装入数据索引减一
        }

        // 将已分配好的桶中数据再倒出来，此时已是对应当前位数有序的表
        for (int i = 0, j = 0; i <= end_index; i++, j++) {
            param[i] = store[j];
        }
    }


    /**
     * 获取指定位上的数值
     */
    private int getDigit(int param, int digit) {
        return (param / radix[digit]) % 10;
    }

}
