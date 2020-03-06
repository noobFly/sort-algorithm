package com.noob.sort;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

import lombok.Builder;
import lombok.Data;

/**
 * 一致性hash 参考： https://www.cnkirito.moe/consistent-hash-lb/
 * <p>
 * 需要考虑： 实现复杂程度、 分布均匀程度、 哈希碰撞概率、 性能
 * <p>
 * 如果用 % 取模 来处理, 算法伸缩性很差， 当服务节点数发生变化时, 与服务器的映射关系会大量失效。
 * <p>
 * 一致性hash则利用hash环对其进行了改进。 一致性哈希将整个哈希值空间组织成一个虚拟的圆环。 服务Node的hash是该圆环上的定位。
 * <p>
 * 用相同的函数Hash计算出哈希值，并确定此数据在环上的位置，从此位置沿环顺时针“行走”，第一台遇到的服务器就是其应该定位到的服务器。
 * 如果服务节点有变动，受影响的数据仅仅是此服务器到其环空间中前一台服务器（即沿着逆时针方向行走遇到的第一台服务器）之间数据，其它不会受到影响
 * <p>
 * 另外，一致性哈希算法在服务节点太少时，容易因为节点分部不均匀而造成数据倾斜问题。
 * 一致性哈希算法引入了虚拟节点机制，即对每一个服务节点计算多个哈希，每个计算结果位置都放置一个此服务节点，称为虚拟节点。具体做法可以在服务器Node后面增加编号来实现。
 * 
 * @author admin
 *
 */
public class ConsistentHashLoadBalance {

	private final ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<String, ConsistentHashSelector>(); // 缓存相同服务及API的loadBalance选择器

	public ServiceInvoker doSelect(List<ServiceInvoker> invokers) { // 同一次的loadBalance的多个invokers除服务提供者不一样，其他的应该是相同的
		ServiceInvoker serviceInvoker = invokers.get(0);
		String key = serviceInvoker.getServiceKey() + "." + serviceInvoker.getMethodName();
		int identityHashCode = System.identityHashCode(invokers);
		ConsistentHashSelector selector = (ConsistentHashSelector) selectors.get(key);
		if (selector == null || selector.identityHashCode != identityHashCode) {
			selectors.put(key, new ConsistentHashSelector(invokers, identityHashCode));
			selector = (ConsistentHashSelector) selectors.get(key);
		}
		return selector.select(serviceInvoker.getArguments());
	}

	/**
	 * <p>
	 * 服务器的访问地址做Node的hash
	 * <p>
	 * 入参列表中指定索引位置的参数作为访问者的hash
	 *
	 */
	@Builder(toBuilder = true)
	@Data
	public static class ServiceInvoker {
		private String serviceKey; // 服务名
		private String serviceIp; // 访问地址
		private String methodName;
		private String hashArguments; // 指定索引位置的参数参与计算hash
		private Object[] arguments; // 参数列表
		private int hashNodes; // 节点个数
	}

	private static final class HashFunction {
		// 按每组4个来构建虚拟服务Node. 因为 Md5是一个16字节长度的数组，将16字节的数组每四个字节一组，分别对应一个虚拟节点
		public static long KetamaHash(byte[] digest, int number) {
			return (((long) (digest[3 + number * 4] & 0xFF) << 24) | ((long) (digest[2 + number * 4] & 0xFF) << 16)
					| ((long) (digest[1 + number * 4] & 0xFF) << 8) | (digest[number * 4] & 0xFF)) & 0xFFFFFFFFL;
		}

		/**
		 * 高运算性能，低碰撞率
		 * <p>
		 * Redis，Memcached，Cassandra，HBase，Lucene 都使用
		 * 
		 * @param origin
		 * @param seed
		 * @return
		 */
		protected long MurmurHash(byte[] origin, int seed) {
			return Math.abs(Hashing.murmur3_32().hashBytes(origin).asInt());
		}

	}

	private static final class ConsistentHashSelector {
		private final TreeMap<Long, ServiceInvoker> virtualServices; // 每个服务实例的hash值映射
		private final int[] argumentIndex; // 指定索引位置的访问参数参与一致性算法
		private final int replicaNumber;
		private final int identityHashCode; // 调用者的id

		ConsistentHashSelector(List<ServiceInvoker> invokerList, int identityHashCode) {
			this.virtualServices = new TreeMap<Long, ServiceInvoker>();
			this.identityHashCode = identityHashCode;

			ServiceInvoker firstInvoker = invokerList.get(0);
			this.replicaNumber = firstInvoker.getHashNodes() < 1 ? 160 : firstInvoker.getHashNodes(); // 默认160虚拟Node
			String[] index = Strings.isNullOrEmpty(firstInvoker.getHashArguments()) ? new String[] { "0" }
					: firstInvoker.getHashArguments().split(",");
			argumentIndex = new int[index.length];
			for (int i = 0; i < index.length; i++) {
				argumentIndex[i] = Integer.parseInt(index[i]);
			}

			// 按每组4个来构建虚拟服务Node. 因为 Md5是一个16字节长度的数组，将16字节的数组每四个字节一组，分别对应一个虚拟节点
			for (ServiceInvoker invoker : invokerList) {
				for (int i = 0; i < replicaNumber / 4; i++) {

					byte[] digest = md5(invoker.getServiceIp() + i); // 可用ip:port 来作为Node
					for (int h = 0; h < 4; h++) {
						long m = HashFunction.KetamaHash(digest, h);
						virtualServices.put(m, invoker);
					}
				}
			}
		}

		public ServiceInvoker select(Object... params) {
			String key = toKey(params);
			byte[] digest = md5(key);
			return selectForKey(HashFunction.KetamaHash(digest, 0));
		}

		private String toKey(Object[] args) {
			StringBuilder buf = new StringBuilder();
			for (int i : argumentIndex) {
				if (i >= 0 && i < args.length) {
					buf.append(args[i]);
				}
			}
			return buf.toString();
		}

		private ServiceInvoker selectForKey(long hash) {
			Map.Entry<Long, ServiceInvoker> entry = virtualServices.tailMap(hash, true).firstEntry();
			if (entry == null) {
				entry = virtualServices.firstEntry();
			}
			return entry.getValue();
		}

		private byte[] md5(String value) {
			MessageDigest md5;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
			md5.reset();
			byte[] bytes;
			try {
				bytes = value.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
			md5.update(bytes);
			return md5.digest();
		}
	}

}
