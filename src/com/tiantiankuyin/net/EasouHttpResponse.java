package com.tiantiankuyin.net;

import java.io.InputStream;

import org.apache.http.HttpStatus;

import com.google.gson.Gson;

/**
 * 封装HTTP服务器响应结果
 * 
 * @author DK
 * 
 * @param <ParseBean>
 */
public class EasouHttpResponse {

	private String requestUrl; // 请求地址

	private InputStream responseStream; // 响应结果输入流

	private byte[] resultData; // 服务器响应数据

	private int responseCode;// 响应码

	private long dataLength;// 响应数据长度

	private boolean isAcceptRanges;// 是否支持断点续传

	private String fileName;// 请求的文件名

	private String contentType;// 请求文件的MIME类型

	/**
	 * HTTP响应码，资源未找到
	 */
	public static final int ADDRESS_NOT_FOUNT = 404;

	/**
	 * HTTP响应码，服务器端出错
	 */
	public static final int SERVER_ERROR = 500;

	/**
	 * 获取请求URL地址
	 * 
	 * @return
	 */
	public String getRequestUrl() {
		return requestUrl;
	}

	/**
	 * 设置请求URL地址
	 * 
	 * @param requestUrl
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * 获取响应输入流数据
	 * 
	 * @return
	 */
	public InputStream getResponseStream() {
		return responseStream;
	}

	/**
	 * 设置响应输入流数据
	 * 
	 * @param responseStream
	 */
	public void setResponseStream(InputStream responseStream) {
		this.responseStream = responseStream;
	}

	/**
	 * 获取服务器响应数据
	 * 
	 * @return
	 */
	public byte[] getResultData() {
		return resultData;
	}

	/**
	 * 设置服务器响应数据
	 * 
	 * @param resultData
	 */
	public void setResultData(byte[] resultData) {

		this.resultData = resultData;
	}

	/**
	 * 获取服务器响应码
	 * 
	 * @return
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * 设置服务器响应码
	 * 
	 * @param responseCode
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * 获取请求文件类型数据长度
	 * 
	 * @return
	 */
	public long getDataLength() {
		return dataLength;
	}

	/**
	 * 设置请求文件类型数据长度
	 * 
	 * @param dataLength
	 */
	public void setDataLength(long dataLength, String contentRange) {
		if (contentRange != null && contentRange.length() > 0) {
			String totalLength = contentRange.substring(contentRange
					.lastIndexOf("/"));
			this.dataLength = Long.parseLong(totalLength);
		}
		this.dataLength = dataLength;
	}

	/**
	 * 获取是否支持断点续传
	 * 
	 * @param isAcceptRanges
	 */
	public boolean isAcceptRanges() {
		return this.isAcceptRanges;
	}

	/**
	 * 设置是否支持断点续传
	 * 
	 * @param acceptRanges
	 *            HTTP响应头 Accept-Ranges值
	 * @param responseCode
	 *            HTTP响应结果码
	 * @return
	 */
	public void setAcceptRanges(String acceptRanges, int responseCode) {
		if ((acceptRanges != null && acceptRanges.equalsIgnoreCase("bytes"))
				|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
			isAcceptRanges = true;
		}
		isAcceptRanges = false;
	}

	/**
	 * 获取文件类型文件名
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件类型文件名
	 * 
	 * @param contentDisposition
	 *            HTTP响应头 Accept-Ranges值
	 */
	public void setFileName(String contentDisposition) {
		if (contentDisposition == null) {
			return;
		}
		contentDisposition = contentDisposition.trim();
		if (contentDisposition == null || contentDisposition.length() <= 0) {
			return;
		}
		String str[] = contentDisposition.split(";");
		if (str != null) {
			for (String string : str) {
				if (string != null && (string = string.trim()).length() > 0) {
					if (string.startsWith("filename=")) {
						String temp = string.substring("filename=".length());
						if (temp != null && temp.startsWith("\"")) {
							temp = temp.substring(1);
						}
						if (temp != null && temp.endsWith("\"")) {
							temp = temp.substring(0, temp.length() - 1);
						}
						this.fileName = temp;
						break;
					}
				}
			}
		}
	}

	/**
	 * 获取文件类型MIME类型
	 * 
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * 设置文件类型MIME类型
	 * 
	 * @param contentType
	 */
	public void setContentType(String contentTypeHeader) {
		this.contentType = contentTypeHeader;
	}

	/**
	 * 获得JSON解析出的bean
	 * 
	 * @return
	 */
	public <Bean> Bean parseResult(Class<Bean> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(new String(this.getResultData()), clazz);
	}
}
