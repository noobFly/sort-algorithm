package com.noob.Introspector;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.function.Supplier;

/**
 * 内省
 * <p>
 * PropertyDescriptor会通过解析Setter和Getter方法，合并解析结果，最终得到对应的PropertyDescriptor实例。
 * <p>
 * 除了Bean属性的之外，还会带有一个属性名为class的PropertyDescriptor实例，它的来源是Class的getClass方法
 * 
 * @author admin
 *
 */
public class TestIntrospector {
	public static String ignoreException(Supplier<String> supplier) {
		try {
			return supplier.get();
		} catch (Exception e) {
			return e.getClass().toString();
		}
	}

	public static void main(String[] args) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(School.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String name = propertyDescriptor.getName();
				System.out.println(name);
				if (!"class".equals(name)) {
					System.out.println(ignoreException(() -> propertyDescriptor.getWriteMethod().getName()));
					System.out.println(ignoreException(() -> propertyDescriptor.getReadMethod().getName()));
				}
				System.out.println("=======================");
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 即使没有成员变量，但又成员方法申明就行
	 *
	 */
	public static class School {

		private String name;
		private int level;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setAge(int age) {
			this.level = age;
		}

		public int getAmount() {
			return 1;
		}

		public int getAddress() {
			return this.level;
		}
	}

}
