package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 插件 Bean
 * 
 * @author DK
 * 
 */
public class Plugin implements Serializable{

	private static final long serialVersionUID = 384377543797664295L;
	
	private long apkID; // apk服务器资源ID
	private String apkName; // apk文件名
	private String className; // 插件类完全限定名
	private String pluginIntent; // 启动插件意图

	public long getApkID() {
		return apkID;
	}

	public void setApkID(long apkID) {
		this.apkID = apkID;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPluginIntent() {
		return pluginIntent;
	}

	public void setPluginIntent(String pluginIntent) {
		this.pluginIntent = pluginIntent;
	}
}
