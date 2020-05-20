package com.noob.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.AopUtils;

import com.noob.proxy.cglib.EntityCglib;
import com.noob.proxy.jdk.EntityJdk;
import com.noob.proxy.jdk.IEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 通过执行发现，A内部调用B 并不会触发拦截器执行。
 * <p>
 * 因为在CglibAopProxy.getProxy创建代理对象使用了CglibAopProxy.DynamicAdvisedInterceptor来包装Callback;
 * CglibAopProxy.ProxyCallbackFilter作为拦截器的过滤器
 * <p>
 * 在执行时，DynamicAdvisedInterceptor.intercept(这个方法体内会通过org.springframework.aop.TargetSource创建原始target对象)
 * ->
 * org.springframework.aop.framework.CglibAopProxy.CglibMethodInvocation.CglibMethodInvocation.proceed()
 * -> org.aopalliance.intercept.MethodInterceptor.invoke (业务自定义) ->
 * CglibAopProxy.CglibMethodInvocation.proceed ->
 * CglibAopProxy.CglibMethodInvocation.invokeJoinpoint
 * <p>
 * 这里最终变成了对真实target对象的org.springframework.cglib.proxy.MethodProxy.invoke
 * <p>
 * 所以 A方法内部执行B方法时 不再触发拦截器
 * 
 * @author admin
 *
 */
@Slf4j
public class SpringAopProxy {
	/**
	 * 
	 * @param bean       真实target对象。
	 * @param cglibProxy 是否开启cglib代理。但真正是否使用需要后续判定
	 * @return
	 */
	public static ProxyFactoryBean proxyFactoryBean(Object bean, boolean cglibProxy) {
		ProxyFactoryBean factory = new ProxyFactoryBean();
		factory.setProxyTargetClass(cglibProxy);
		factory.addAdvice(new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				log.info("MethodInterceptor invoke: {}", invocation.getMethod().getName());
				return invocation.proceed();
			}
		});
		factory.setTarget(bean);
		return factory;
	}

	public static void main(String[] args) {
		testInterface(true);

		testClass(true);
	}

	/**
	 * AopUtils 的代理判定前提是： 一定要满足对象是 org.springframework.aop.SpringProxy
	 * <p>
	 * 代理对象是： com.noob.proxy.jdk.EntityJdk$$EnhancerBySpringCGLIB$$1b6a1486
	 */
	private static void testInterface(boolean isProxyTargetClass) {
		IEntity proxy = (IEntity) SpringAopProxy.proxyFactoryBean(new EntityJdk(), isProxyTargetClass).getObject();
		log.info(
				"Interface: Target:{}, Proxy:{},  isProxyTargetClass: {}, isAopProxy:{}, isJdkDynamicProxy:{}, isCglibProxy:{}",
				AopUtils.getTargetClass(proxy).getName(), proxy.getClass().getName(), isProxyTargetClass,
				AopUtils.isAopProxy(proxy), AopUtils.isJdkDynamicProxy(proxy), AopUtils.isCglibProxy(proxy));
		proxy.test();
	}

	private static void testClass(boolean isProxyTargetClass) {
		EntityCglib proxy = (EntityCglib) SpringAopProxy.proxyFactoryBean(new EntityCglib(), isProxyTargetClass)
				.getObject();
		log.info(
				"Class: Target:{}, Proxy:{},  isProxyTargetClass: {}, isAopProxy:{}, isJdkDynamicProxy:{}, isCglibProxy:{}",
				AopUtils.getTargetClass(proxy).getName(), proxy.getClass().getName(), isProxyTargetClass,
				AopUtils.isAopProxy(proxy), AopUtils.isJdkDynamicProxy(proxy), AopUtils.isCglibProxy(proxy));
		proxy.test();
	}
}
