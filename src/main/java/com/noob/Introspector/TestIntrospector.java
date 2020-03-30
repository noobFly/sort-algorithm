package com.noob.Introspector;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * 内省
 * <p>
 * PropertyDescriptor会通过解析Setter和Getter方法，合并解析结果，最终得到对应的PropertyDescriptor实例。
 * <p>
 * 除了Bean属性的之外，还会带有一个属性名为class的PropertyDescriptor实例，它的来源是Class的getClass方法，
 * 
 * @author admin
 *
 */
public class TestIntrospector {
	public static void main(String[] args) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(School.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String name = propertyDescriptor.getName();
				System.out.println(name);
				if (!"class".equals(name)) {
					System.out.println(propertyDescriptor.getWriteMethod().getName());
					System.out.println(propertyDescriptor.getReadMethod().getName());
				}
				System.out.println("=======================");
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 即使没有成员变量age
	 * <p>
	 * 有成员方法setAge： 内省结果： PropertyDescriptor.getName() 返回age。
	 * PropertyDescriptor.getWriteMethod()返回setAge
	 * <p>
	 * 有成员方法getAmount： 内省结果： PropertyDescriptor.getName()
	 * 返回amount。propertyDescriptor.getReadMethod()返回getAmount
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
	}

}
