package com.tiantiankuyin.net;

/**
 * HTTP请求异常封装
 * 
 * @author DK
 * 
 */
public class EasouHttpException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * IO异常，无法开启连接
	 */
	public static final int NO_NETWORK = 0x1;

	/**
	 * URL错误
	 */
	public static final int MALFORMED_URL_EXCEPTION = 0x2;

	/**
	 * IO异常，无法开启连接
	 */
	public static final int IO_EXCEPTION = 0x3;

	/**
	 * 网络异常
	 */
	public static final int NET_WORK_EXCEPTION = 0x4;

	/**
	 * 异常类型
	 */
	private int exceptionType;
	
	/**
	 * 异常信息
	 */
	private String exceptionMessage;
	
	/**
	 * HTTP请求异常
	 * 
	 * @param type
	 *            异常类型
	 */
	public EasouHttpException(int type) {
		super();
		this.exceptionType = type;
	}

	/**
	 * HTTP请求异常
	 * 
	 * @param type
	 *            异常类型
	 */
	public EasouHttpException(int type, String exceptionMessage) {
		super();
		this.exceptionType = type;
		this.exceptionMessage = exceptionMessage;
	}

	/**
	 * 获取异常类型
	 * 
	 * @return
	 */
	public int getExceptionType() {
		return exceptionType;
	}
	
	/**
	 * 获取异常信息
	 * @return
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	/**
	 * 设置异常信息
	 * @param exceptionMessage
	 */
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
