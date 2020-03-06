package com.noob.sort;

import java.util.Random;
import java.util.TreeMap;

/**
 * TreeMap的KEY需要实现{@link Comparable}接口
 * 
 * @author admin
 *
 */
public class RandomLoadBalance {
	static int loop_max = 1000;
	static double[] weightDouble = new double[] { 0.06, 2.94, 97 };
	static int[] weightInt = new int[] { 1, 1, 3 };

	/** TreeMap.ceilingKey 返回指定key的项, 如果没有则返回第一个大于指定KEY的 key **/
	public static void sortByTreeMap() {

		int choose1 = 0, choose2 = 0, choose3 = 0;
		TreeMap<Double, Integer> map = new TreeMap<Double, Integer>();
		double weightTotal = 0;
		for (int index = 0; index < weightDouble.length; index++) {
			weightTotal += weightDouble[index];
			map.put(weightTotal, index + 1);
		}
		int loop = loop_max;

		while (loop > 0) {
			double random = (Math.random() * weightTotal);
			// returns the entry for the least key greater than the specified key
			Integer index = map.get(map.ceilingKey(random));
			if (1 == index) {
				choose1++;
			} else if (2 == index) {
				choose2++;
			} else if (3 == index) {
				choose3++;
			}
			--loop;
		}

		System.out.println(String.format("index1-%s index2-%s index3-%s", choose1, choose2, choose3));

	}

	/**
	 * 
	 * 大致思路是：
	 * <p>
	 * 在横轴上以权重值为定位点，[ 0, 权重累加值 ] 为随机数范围。 随机值落点入横轴。
	 * <p>
	 * ( 0+weight1+...+weight(N-1), 0+weight1+...+weight(N-1)+weightN ] 标识
	 * weightN被命中
	 *
	 */

	/**
	 * 1）假设有四个集群节点A,B,C,D,对应的权重分别是1,2,3,4,
	 * <p>
	 * 那么请求到A节点的概率就为1/(1+2+3+4) = 10%. B,C,D节点依次类推为20%,30%,40%.
	 * <p>
	 * 2）总权重为10(1+2+3+4), 根据10随机出一个整数,假如为随机出来的是2.然后依次和权重相减,比如2(随机数)-1(A的权重) = 1,
	 * <p>
	 * 然后1(上一步计算的结果)-2(B的权重) = -1,此时-1 < 0,那么则调用B,其他的以此类推
	 * 
	 */
	public static void sortInt() {

		int totalWeight = 0;
		boolean isAllSameWeight = true; // 判定权重值是否都是一样的
		int length = weightInt.length;
		for (int i = 0; i < length; i++) {
			int weight = weightInt[i];
			totalWeight += weight;
			if (isAllSameWeight && i > 0 && weight != weightInt[i - 1]) {
				isAllSameWeight = false;
			}
		}
		Random random = new Random();

		int choose1 = 0, choose2 = 0, choose3 = 0;
		int turn = loop_max;

		while (turn > 0) {
			int index = -1;
			if (totalWeight > 0 && !isAllSameWeight) {
				int offset = random.nextInt(totalWeight);
				for (int i = 0; i < length; i++) {
					offset -= weightInt[i];
					if (offset < 0) {
						index = i;
						break;
					}
				}
			}
			// 权重值都一样则随机任何一个数值
			if (index == -1) {
				index = random.nextInt(length);
			}
			if (0 == index) {
				choose1++;
			} else if (1 == index) {
				choose2++;
			} else if (2 == index) {
				choose3++;
			}
			--turn;
		}
		System.out.println(String.format("index1-%s index2-%s index3-%s", choose1, choose2, choose3));

	}
}