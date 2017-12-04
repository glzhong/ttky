/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 单个歌词对象 其中包括了歌词的开始时间，结束时间
 * @author Erica 
 *  */
public class Sentence implements Serializable
{

	private static final long serialVersionUID = -4068339514384606735L;
	
	/** 行起始时间 */
	private long fromTime;
	/** 行结束时间 */
	private long toTime;
	/** 行内容 */
	private String content;

	public Sentence(String content, long fromTime, long toTime)
	{
		this.content = content;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}

	public Sentence(String content, long fromTime)
	{
		this(content, fromTime, 0);
	}

	public Sentence(String content)
	{
		this(content, 0, 0);
	}

	/** 获取开始时间 
	 * @return fromTime
	 * */
	public long getFromTime()
	{
		return fromTime;
	}

	/** 设置开始时间 
	 * @param fromTime long 开始时间
	 * */
	public void setFromTime(long fromTime)
	{
		this.fromTime = fromTime;
	}

	/** 获取结束时间
	 * @return toTime
	 *  */
	public long getToTime()
	{
		return toTime;
	}

	/** 设置结束时间
	 * @param toTime long 结束时间
	 *  */
	public void setToTime(long toTime)
	{
		this.toTime = toTime;
	}

	/** 是否在行时间内
	 * @param time long 计算时间对象
	 *  */
	public boolean isInTime(long time)
	{
		return time >= fromTime && time <= toTime;
	}

	/** 获取歌词内容 
	 * @return content
	 * */
	public String getContent()
	{
		return content;
	}

	/** 获取歌词持续时间
	 * @return 持续时间
	 *  */
	public long getDuring()
	{
		return toTime - fromTime;
	}

	/** 整合时间字符串
	 *  */
	public String toString()
	{
		return "{" + fromTime + "(" + content + ")" + toTime + "}";
	}
}
