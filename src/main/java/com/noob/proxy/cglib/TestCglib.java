package com.noob.proxy.cglib;

/**
 * 私有方法直接编译报错, 因为私有权限只能本类使用
 *
 */
public class TestCglib {
	public static void main(String[] args) {
		EntityCglib entity = EntityCglib.getCglibInstance(new EntityInterceptor(new EntityCglib()));
		entity.test();
		entity.testdefault();
		// entity.testprivate();
		entity.teststatic();
		entity.testfinal();
		entity.testProtected();
	}
}
