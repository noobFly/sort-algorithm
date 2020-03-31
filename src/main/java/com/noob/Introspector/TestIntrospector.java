package com.noob.Introspector;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.function.Supplier;

/**
 * 内省
 * <p>
 * PropertyDescriptor会通过解析public的
 * Setter及Getter方法（含父类），合并解析结果，最终得到对应的PropertyDescriptor实例。
 * <P>
 * 以 public的Getter、Setter成员方法 为准， 不管成员变量有没有
 * <p>
 * 除了Bean属性的之外，还会带有一个属性名为class的PropertyDescriptor实例，它的来源是Class的getClass方法
 * 
 *
 */
public class TestIntrospector {
	public static String ignoreException(Supplier<String> supplier) {
		try {
			return supplier.get();
		} catch (Exception e) {
			return e.getClass().getName().toString();
		}
	}

	public static void main(String[] args) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(University.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String name = propertyDescriptor.getName();
				System.out.println("Name:   " + name);
				System.out.println(
						"WriteMethod:   " + ignoreException(() -> propertyDescriptor.getWriteMethod().getName()));
				System.out.println(
						"ReadMethod:   " + ignoreException(() -> propertyDescriptor.getReadMethod().getName()));
				System.out.println("=======================");
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

	}

	public static class School {
		protected int level;

		public int getParentAmount() {
			return 1;
		}

		public int getParentAddress() {
			return this.level;
		}

		protected int getParentCity() {
			return this.level;
		}
	}

	public static class University extends School {

		private String title;

		public void setAge(int age) {
			this.level = age;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		private int getLevel() {
			return this.level;
		}

		int getFun() {
			return 1;
		}
	}

}
