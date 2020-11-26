package com.noob.spring.beanDefinition;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.FactoryBean;

import lombok.Getter;
import lombok.Setter;

public class CustomizerClientFactoryBean<T> implements FactoryBean<T> {
	@Getter
	@Setter
	private Class<T> clas;
	@Getter
	@Setter
	private boolean proxyTargetClass = false;

	public CustomizerClientFactoryBean(Class<T> clas) {
		this.clas = clas;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getObject() throws Exception {
		return proxyTargetClass ? createProxy(clas)
				: (T) Proxy.newProxyInstance(clas.getClassLoader(), new Class<?>[] { clas },
						new CustomizerClientProxy(clas));
	}

	T createProxy(Class<T> clas) {
		ProxyFactoryBean factory = new ProxyFactoryBean();
		factory.setProxyTargetClass(true);
		factory.addAdvice(new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				return new CustomizerClientProxy(clas);
			}
		});
		factory.setTargetClass(clas);
		return (T) factory.getObject(); // 执行DefaultAopProxyFactory.createAopProxy 会选择走JdkDynamicAopProxy或ObjenesisCglibAopProxy
	}

	@Override
	public Class<?> getObjectType() {
		return clas;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
