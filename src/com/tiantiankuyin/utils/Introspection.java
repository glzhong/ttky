package com.tiantiankuyin.utils;

import java.lang.reflect.Field;

/**
 * 内省
 * 
 * @author Administrator
 * 
 */
public class Introspection<T> {
	/** 需要内省的对象 */
	private Object object;  
	/** 需要内省对象的字段名 */
	private String fieldName;  
	/** 当前内省字段 */
	private Field currentField;  
	/** 是否已经对当前对象进行初始化 */
	private boolean isInit;  

	public Introspection(Object object, String fieldName) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null");
		}
		this.object = object;
		this.fieldName = fieldName;
	}

	/**
	 * 在字段内省前，对字段进行破解
	 */
	private void prepare() {
		if (isInit)
			return;
		isInit = true;

		Class<?> clazz = object.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				currentField = field;
				return;
			} catch (Exception e) {
			} finally {
				clazz = clazz.getSuperclass();
			}
		}
	}

	/**
	 * get方法，针对当前object对象中的currentField对象
	 * 
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public T get() throws NoSuchFieldException, IllegalAccessException,
			IllegalArgumentException {
		prepare();

		if (currentField == null)
			throw new NoSuchFieldException();

		try {
			@SuppressWarnings("unchecked")
			T t = (T) currentField.get(object);
			return t;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("unable to cast object");
		}
	}

	public void set(T value) throws NoSuchFieldException,
			IllegalAccessException, IllegalArgumentException {
		prepare();

		if (currentField == null)
			throw new NoSuchFieldException();

		currentField.set(object, value);
	}
}
