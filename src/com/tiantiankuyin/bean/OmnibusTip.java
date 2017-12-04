package com.tiantiankuyin.bean;

import java.io.Serializable;

/** 精选集标签类对象 
 * @author Erica
 * */
public class OmnibusTip implements Serializable {
	private static final long serialVersionUID = 7276956632478993496L;
	/** 标签ID */
	private int id;
	/** 标签名 */
	private String name;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null) {
			OmnibusTip another = (OmnibusTip) o;
			if (another != null && another.getId() > 0
					&& this.getId()>0 && this.getId() == another.getId())
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if(id>0){
			return this.id;
		}
		return 0;
	}
}
