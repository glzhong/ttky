package com.tiantiankuyin.database.dao.factory;

import java.lang.reflect.Constructor;

/**
 * 数据库泛型工厂 该工厂用于根据Dao接口生成相应的Dao实现类
 * 
 * @author DK
 * 
 */
public class DatabaseDaoFactory {

	private DatabaseDaoFactory() {
	}

	private static DatabaseDaoFactory factory;

	/**
	 * 获取数据库Dao工厂实例
	 * 
	 * @return
	 */
	public static DatabaseDaoFactory newInstance() {
		if (factory == null) {
			synchronized (DatabaseDaoFactory.class) {
				if (factory == null) {
					factory = new DatabaseDaoFactory();
				}
			}
		}
		return factory;
	}

	/**
	 * 返回相应数据库查询Dao实例
	 * 
	 * @param clazz
	 *            Dao的class
	 * @param constructorParameters
	 *            构造函数参数，如果是无参构造则传递null
	 * @param parameterTypes
	 *            Dao构造函数形参类型，如果是无参构造则传递null
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T createDao(Class<T> clazz, Object[] constructorParameters,
			Class<?>... parameterTypes) throws Exception {
		String className = clazz.getName();
		if (parameterTypes == null)
			return (T) Class.forName(className).newInstance();
		else {
			Constructor<?> constructor = Class.forName(className)
					.getConstructor(parameterTypes);
			return (T) constructor.newInstance(constructorParameters);
		}
	}
}
