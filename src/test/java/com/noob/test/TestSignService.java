package com.noob.test;

import com.alibaba.fastjson.JSON;
import com.noob.contract.sign.SignService;
import com.noob.contract.sign.domain.ApplyLimit;
import com.noob.contract.sign.impl.SignServiceImpl;

public class TestSignService {
	public static void main(String[] args) throws Exception {
		SignService service = new SignServiceImpl();
		ApplyLimit domain = new ApplyLimit();
		domain.setCertificateNo("CN123456789");
		domain.setChannel("30");
		domain.setCustomerId(100L);
		domain.setFlowNo("flowNo123456788");
		domain.setLoanType("XXL-001");
		String templateParameters = "templateInput.flowNo,applyLimit.loanType,channel,customer.customerName,customer.certificateNo";
		System.out.println(JSON.toJSONString(service.sign(domain, templateParameters)));
	}
}
