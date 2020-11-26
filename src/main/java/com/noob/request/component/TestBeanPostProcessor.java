package com.noob.request.component;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

 @Component
public class TestBeanPostProcessor implements BeanPostProcessor {
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.equals("CService")) {
			return new String(); //只有完全没有被其他类引用的bean才能换类型。否则在引用校验类型时会报错
		}
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.equals("CService")) {
			return new String();
		}
		return bean;
	}
}
