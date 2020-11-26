package com.noob.spring.beanDefinition;

import com.noob.request.component.BService;

@CustomizerClient(targetClass = BService.class, proxyTargetClass = true)
public interface ITest {

}
