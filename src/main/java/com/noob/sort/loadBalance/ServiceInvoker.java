package com.noob.sort.loadBalance;

import lombok.Builder;
import lombok.Data;

/**
 *
 * <p>
 * 服务器的访问地址做Node的hash
 * <p>
 * 入参列表中指定索引位置的参数作为访问者的hash
 *
 */
@Builder(toBuilder = true)
@Data
public class ServiceInvoker {
	public static int DEFAULT_NODE = 160;
	public static String[] DEFAULT_INDEX = new String[] { "0" };

	private String applicationName; // 服务名
	private String serviceIp; // 服务地址
	private String methodName; // API
	private int weight; // 权重
	/**
	 * 一致性hash算法内使用
	 */
	private String hashArguments; // 指定索引位置的参数参与计算hash
	private final int nodes = DEFAULT_NODE; // 节点个数 <越大越平衡、越均匀>

	/**
	 * 
	 * @return 标识唯一服务API
	 */
	public String getKey() {
		return this.getApplicationName() + "." + this.getMethodName();
	}
}