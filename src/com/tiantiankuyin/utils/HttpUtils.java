package com.tiantiankuyin.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HttpUtils {
	public static String req(String requestUrl) throws MalformedURLException, IOException {	
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("GET"); // 请求方式
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			String result = "";
			if(conn.getResponseCode()==200) {
				InputStream in = conn.getInputStream(); 
				BufferedReader breader = new BufferedReader(new InputStreamReader(in)); 
				
				String line = "";
				StringBuilder sb = new StringBuilder();
				while ((line = breader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
			}
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new MalformedURLException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}

}
