package com.noob.request.component;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


import lombok.Getter;

/**
 * 验证执行顺序
 * 
 * @author admin
 *
 */
// @Component
public class ExecuteSortComponent implements BeanFactoryAware, ApplicationContextAware, InitializingBean {
	@Getter
	private BService service;

	public ExecuteSortComponent(BService b) {
		System.out.println("这里是Constructor");
	}

	@PostConstruct
	public void init() {
		System.out.println("这里是@PostConstruct");
	}

	@Autowired
	public void setService(BService service) {
		this.service = service;
		System.out.println("这里是@Autowired");

	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("这里是BeanFactoryAware");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("这里是InitializingBean");

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("这里是applicationContext");
	}
}
