package com.tiantiankuyin.parser;

/**
 * JSON解析器基类在解析JSON时只需继承该类，将希望得到的bean作为解析器的泛型类型即可。
 * 
 * @author DK
 * 
 * @param <ParseBean>
 */
public interface EasouResultParser<ParseBean> {

	/**
	 * 从json中解析出相应的对象
	 * 
	 * @param json
	 * @return
	 */
	public abstract ParseBean parseResult(String json);
}
