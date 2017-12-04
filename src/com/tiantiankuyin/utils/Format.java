package com.tiantiankuyin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {
	public static String getDatetimeMillisecond(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return df.format(date);
	}
	
	public static String getDatetimeMillisecond() {
		return getDatetimeMillisecond(new Date());
	}
	
	public static String getDate(Date date){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}
	
	public static String getDate() {
		return getDate(new Date());
	}
	
}
