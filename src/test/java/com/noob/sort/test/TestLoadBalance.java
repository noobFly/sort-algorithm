package com.noob.sort.test;

import java.util.List;

import com.google.common.collect.Lists;
import com.noob.sort.ConsistentHashLoadBalance;
import com.noob.sort.ConsistentHashLoadBalance.ServiceInvoker;
import com.noob.sort.RandomLoadBalance;

public class TestLoadBalance {
	public static void main(String[] args) {
		testRandomLoadBalance();
		testConsistentHashLoadBalance();
	}

	private static void testRandomLoadBalance() {
		RandomLoadBalance.sortByTreeMap();
		RandomLoadBalance.sortInt();
	}

	private static void testConsistentHashLoadBalance() {
		ConsistentHashLoadBalance consistentHashLoadBalance = new ConsistentHashLoadBalance();
		String[] ServiceIps = new String[] { "127.0.0.1:8080", "189.84.255.216:8080", "172.164.11.144:8080" };
		Object[][] params = new Object[][] { { "noticeID_2354", "WEIXIN", "10", "100" },
				{ "interestID_1313", "WEIBO", "3", "20" }, { "historyID_9876", "ZHIHU", "2", "10" },
				{ "forkID_5476", "DOUXIN", "29", "10" } };
		String[] methodNames = new String[] { "queryNotice", "queryInterest", "queryHistory", "queryFork" };
		for (int j = 0; j < params.length; j++) {
			List<ServiceInvoker> invokerList = Lists.newArrayList();

			for (int i = 0; i < ServiceIps.length; i++) {
				invokerList.add(ServiceInvoker.builder().serviceIp(ServiceIps[i]).methodName(methodNames[j])
						.arguments(params[j]).hashArguments("0,1").serviceKey("testDubboConsistentHashLoadBalance")
						.build()); // 同一次的loadBalance的多个invokers除服务提供者不一样，其他的应该是相同的
			}

			System.out.println(consistentHashLoadBalance.doSelect(invokerList).getServiceIp());
		}

	}
}
