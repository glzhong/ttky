package com.tiantiankuyin;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetRequestHelp {
	
	
	 private static AsyncHttpClient client=new AsyncHttpClient();//实例化浏览器对象
	 
	 static{
		 client.setTimeout(11000); //设置链接超时，默认为10秒
		 
		 
	 }
	 
	 public static void get(String requestUrl,AsyncHttpResponseHandler handler){
		 client.get(requestUrl, handler); //用一个完整的url获取一个String对象
	 }
	 
	 public static void get(String requestUrl,RequestParams requestParams,AsyncHttpResponseHandler handler){
		 client.get(requestUrl, requestParams, handler); //url带参数
	 }
	 
	 public static void get(String reuqestUrl,JsonHttpResponseHandler handler){
		 client.get(reuqestUrl, handler);
	 }
	 
	 public static void get(String requstUrl,RequestParams requestParam,JsonHttpResponseHandler handler){
		 client.get(requstUrl, requestParam,handler);
	 }
	 
	 public static void get(String requestUrl,BinaryHttpResponseHandler handler){
		 client.get(requestUrl, handler);
	 }
	 
	 public static AsyncHttpClient getClient(){
		 return client;
	 }
	

}
