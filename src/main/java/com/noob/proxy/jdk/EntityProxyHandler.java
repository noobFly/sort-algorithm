package com.noob.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityProxyHandler implements InvocationHandler {
	private IEntity entity;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("proxy invoke. method:" + method.getName());
		return method.invoke(entity, args);
	}
}
