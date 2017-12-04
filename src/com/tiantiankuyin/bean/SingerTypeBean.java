package com.tiantiankuyin.bean;

import java.io.Serializable;

/** 歌手类别对象
 * @author Erica
 *  */
public class SingerTypeBean implements Serializable{

	private static final long serialVersionUID = -2566041641154829132L;
	/** 图片资源id */
	private int resId;
	/** 歌手类别名称  */
	private String name;
	/** 歌手类别  */
	private int typeId;
	
	public SingerTypeBean(){
	}
	public SingerTypeBean(int resId,String name,int typeId){
		this.resId=resId;
		this.name=name;
		this.typeId=typeId;
	}
	public int getResId() {
		return resId;
	}
	public void setResId(int resId) {
		this.resId = resId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
}
