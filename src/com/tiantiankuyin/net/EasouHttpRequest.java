package com.tiantiankuyin.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpStatus;

import android.content.Context;

import com.tiantiankuyin.utils.CommonUtils;

/**
 * 封装HTTP请求
 * 
 * 当需要HTTP请求时均可使用该类。使用方法：创建该类的实例对象其中泛型类型为期望服务器得到返回结果的泛型bean
 * 如果不需要得到服务器返回结果可以为null或是给出Object。实例化该类的时候需要增加一个匿名内部类并重写以下三个方法
 * 1、onFinish(EasouHttpResponse<ParseBean> response);
 * 得到服务器返回结果，结果数据均封装在EasouHttpResponse中
 * 。如果希望解析bean，则调用EasouHttpResponse的parseResult方法 2、EasouResultParser<ParseBean>
 * createParser(); 创建对象解析器 3、onError(EasouHttpException exception); 异常情况处理
 * 在使用该类时不必新开线程运行，该类的所有HTTP任务均有线程池统一管理
 * 
 * @author DK
 */
public abstract class EasouHttpRequest extends
		EasouAsyncTask<Object, Object, EasouHttpResponse> {

	/**
	 * HTTP异步请求任务
	 * 
	 * @param requestUrl
	 *            请求服务器地址 HTTP异步请求任务
	 * @param requestUrl
	 *            请求服务器地址
	 * @param requestParams
	 *            HTTP请求头参数
	 * @param requestMethod
	 *            请求方式
	 * @param requestHeaders
	 *            HTTP请求头参数
	 * @param isGzip
	 *            HTTP请求方式是否是GZIP
	 */
	public EasouHttpRequest(Context context, String requestUrl,
			HttpRequestMethod requestMethod,
			Map<String, String> requestHeaders, boolean isGzip) {
		super();
		this.mContext = context;
		this.requestUrl = requestUrl;
		this.requestMethod = requestMethod;
		this.requestHeaders = requestHeaders;
		this.isGzip = isGzip;
		this.execute(new Object[] {});
	}

	/**
	 * HTTP异步请求任务
	 * 
	 * @param requestUrl
	 *            请求服务器地址 HTTP异步请求任务
	 * @param requestUrl
	 *            请求服务器地址
	 * @param requestParams
	 *            HTTP请求头参数
	 * @param requestMethod
	 *            请求方式
	 * @param requestHeaders
	 *            HTTP请求头参数
	 * @param requestParams
	 *            HTTP请求参数
	 * @param isGzip
	 *            HTTP请求方式是否是GZIP
	 */
	public EasouHttpRequest(Context context, String requestUrl,
			HttpRequestMethod requestMethod,
			Map<String, String> requestHeaders, byte[] requestParams,
			boolean isGzip) {
		super();
		this.mContext = context;
		this.requestUrl = requestUrl;
		this.requestMethod = requestMethod;
		this.requestHeaders = requestHeaders;
		this.requestParams = requestParams;
		this.isGzip = isGzip;
		this.execute(new Object[] {});
	}

	/**
	 * 上下文对象
	 */
	private Context mContext;

	/**
	 * 请求服务器地址
	 */
	private String requestUrl;

	/**
	 * HTTP请求方式
	 */
	private HttpRequestMethod requestMethod;

	/**
	 * HTTP请求头参数
	 */
	private Map<String, String> requestHeaders;

	/**
	 * HTTP请求参数
	 */
	private byte[] requestParams;

	/**
	 * HTTP请求方式是否是GZIP
	 */
	private boolean isGzip;

	/**
	 * HTTP连接
	 */
	private HttpURLConnection conn;

	/**
	 * 响应数据流
	 */
	private InputStream responseStream;

	/**
	 * 连接超时时间
	 */
	public static final int CONNECT_TIMEOUT = 15000;

	/**
	 * 读取超时时间
	 */
	public static final int REQUEST_TIMEOUT = 200000;

	@Override
	protected EasouHttpResponse doInBackground(Object... params)
			throws EasouHttpException {
		return startConnection();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(EasouHttpResponse response) {
		onFinish(response);
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	/**
	 * 开始HTTP连接
	 * 
	 * @param params
	 *            请求服务器接口参数
	 * @throws EasouHttpException
	 */
	private synchronized EasouHttpResponse startConnection() {
		EasouHttpResponse response = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			if (mContext == null) {
				return null;
			}
			if (!CommonUtils.isHasNetwork(mContext)) { // 当前无网络
				onError(new EasouHttpException(EasouHttpException.NO_NETWORK));
				return null;
			}
			URL url = new URL(this.requestUrl);
//			if (CommonUtils.isWapConnected(mContext)) { // 如果当前是wap连接
//				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP,
//						new InetSocketAddress(Easou.proxyHost, Easou.proxyPort));
//				conn = (HttpURLConnection) url.openConnection(proxy);
//			} else {
//				conn = (HttpURLConnection) url.openConnection();
//			}
			conn = CommonUtils.getConnection(this.requestUrl, mContext);
			conn.setRequestMethod(this.requestMethod.toString()); // 设置请求方式
			conn.setDoInput(true);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(REQUEST_TIMEOUT);
			if (this.requestHeaders != null && this.requestHeaders.size() > 0) { // 设置HTTP请求头参数
				addHttpHeaders(conn);
			}
			if (this.requestMethod == HttpRequestMethod.POST) { // 如果是POST请求，则将HTTP请求参数写入请求流
				setRequestParams(conn);
			}
			if (this.isGzip) {
				conn.setRequestProperty("Accept-Encoding", "gzip");
			}
			conn.connect();
			response = new EasouHttpResponse();

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpStatus.SC_OK
					|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
				responseStream = conn.getInputStream();
				if (responseStream == null) {
					onError(new EasouHttpException(
							EasouHttpException.NET_WORK_EXCEPTION));
					return null;
				}
				response.setRequestUrl(this.requestUrl);
				response.setResponseStream(responseStream);
				response.setResponseCode(responseCode);
				response.setDataLength(conn.getContentLength(),
						conn.getHeaderField("Content-Range"));
				response.setAcceptRanges(conn.getHeaderField("Accept-Ranges"),
						responseCode);
				response.setFileName(conn.getHeaderField("Content-Disposition"));
				response.setContentType(conn.getHeaderField("content-type"));
				bis = new BufferedInputStream(responseStream);
				bis.mark(2);
				byte[] header = new byte[2];
				int length = bis.read(header);
				bis.reset();
				// 判断是否是GZIP格式
				if (length != -1 && getShort(header) == 0x1f8b) {
					bis = new BufferedInputStream(new GZIPInputStream(bis));
//					responseStream = new GZIPInputStream(bis);
				}
				bos = new ByteArrayOutputStream();
				byte[] bytes = new byte[1024];
				int len = -1;
				while ((len = bis.read(bytes)) != -1) {
					bos.write(bytes, 0, len);
				}
				response.setResultData(bos.toByteArray());
			} else if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| responseCode == HttpStatus.SC_MOVED_TEMPORARILY) {// 判断是否是301或者302,重定向
				String location = conn.getHeaderField("Location");
				if (location != null) {
					disposeConnectionResource();
					return startConnection();
				}
			}
		} catch (MalformedURLException e) {
			onError(new EasouHttpException(
					EasouHttpException.MALFORMED_URL_EXCEPTION, e.getMessage()));
		} catch (IOException e) {
			onError(new EasouHttpException(EasouHttpException.IO_EXCEPTION,
					e.getMessage()));
		} finally {
			try {
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				//e.printStackTrace();
			} finally {
				try {
					if (bis != null)
						bis.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			if (responseStream != null) {
				try {
					responseStream.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			if (this.conn != null) {
				this.conn.disconnect();
				conn = null;
			}
		}
		return response;
	}

	/**
	 * 添加请求参数，直接写入请求数据流中
	 * 
	 * @throws IOException
	 */
	private void setRequestParams(HttpURLConnection conn) throws IOException {
		BufferedOutputStream paramsBOS = new BufferedOutputStream(
				conn.getOutputStream());
		paramsBOS.write(requestParams);
		paramsBOS.close();
	}

	/**
	 * 添加HTTP请求头参数
	 */
	private void addHttpHeaders(HttpURLConnection conn) {
		for (String header : this.requestHeaders.keySet()) {
			conn.addRequestProperty(header, this.requestHeaders.get(header));
		}
	}

	private static int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}
	
	/**
	 * HTTP请求方式枚举
	 * 
	 * @author DK
	 * 
	 */
	public enum HttpRequestMethod {
		/**
		 * GET请求方式
		 */
		GET,

		/**
		 * POST请求方式
		 */
		POST
	}

	/**
	 * 释放连接资源
	 */
	public void disposeConnectionResource() {
		if (responseStream != null) {
			try {
				responseStream.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		if (this.conn != null) {
			this.conn.disconnect();
			conn = null;
		}
	}

	/**
	 * 请求结束方法,请求者重写该方法可以做请求完成处理工作
	 * 
	 * @param response
	 */
	protected abstract void onFinish(EasouHttpResponse response);

	/**
	 * 请求错误处理，请求者重写该方法可以获得请求服务器异常信息
	 * 
	 * @param exception
	 */
	protected abstract void onError(EasouHttpException exception);
}
