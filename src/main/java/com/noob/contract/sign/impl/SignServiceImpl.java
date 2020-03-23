package com.noob.contract.sign.impl;

import java.util.Map;

import com.noob.contract.sign.SignService;
import com.noob.contract.sign.domain.ApplyLimit;
import com.noob.contract.sign.domain.TemplateInput;
import com.noob.contract.sign.handler.ApplyLimitTemplateParamsHandler;
import com.noob.contract.sign.handler.TemplateParamsHandler;

public class SignServiceImpl implements SignService {

	private TemplateParamsHandler handler = new ApplyLimitTemplateParamsHandler();

	@Override
	public Map<String, Object> sign(ApplyLimit domain, String templateParameters) throws Exception {
		TemplateInput input = new TemplateInput();
		input.setFlowNo(domain.getFlowNo());
		input.setChannel(domain.getChannel());
		input.setLoanType(domain.getLoanType());
		input.setTemplateParams(templateParameters);
		handler.setDefaultDomain(domain);
		return handler.handle(input);

	}

}
