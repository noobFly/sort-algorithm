package com.noob.proxy.cglib;

/**
 * 私有方法直接编译报错
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
