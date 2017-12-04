package com.tiantiankuyin.database.bll;

import android.content.Context;
import android.os.Handler;

/**
 * 当数据准备好后的监听者
 * 此类仅可用于有Looper的线程中调用，比如UI线程，Service线程等
 * @author looming
 * 
 * @param <T>
 */
public abstract class OnDataPreparedListener<T> {

	/**
	 * data可能为 null，onDataPrepared中的代码，将在主线程
	 * @param data
	 */
	public abstract void onDataPrepared(T data);

	/**
	 * 通知UI
	 * 此方法仅由<B>数据库框架</B>调用
	 * @param context
	 * @param l
	 */
	/* package */void callback(final Context context, final T data) {

		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				OnDataPreparedListener.this.onDataPrepared(data);
			}
		});
	}
}
