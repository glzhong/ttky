package com.tiantiankuyin.download;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件任务线程池
 * 
 * @author DK
 * 
 */
public class DownloadThreadPool {

	private static final String TAG = "Download";

	/** 线程池中最小线程数 */
	private static final int CORE_POOL_SIZE = 3;

	/** 线程池中最大线程数 */
	private static final int MAXIMUM_POOL_SIZE = 5;

	/** 空闲线程存活时间 */
	private static final int KEEP_ALIVE = 10;

	/** 任务队列 */
	private static final LinkedBlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<Runnable>();

	/** 线程池工厂 */
	private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable runnable) {
			return new Thread(runnable, TAG + mCount.getAndIncrement());
		}
	};

	/** 拒绝任务处理消息 */
	private static final AbortPolicy HANDLER = new ThreadPoolExecutor.AbortPolicy() {

		@Override
		public void rejectedExecution(Runnable runnable, ThreadPoolExecutor e) {
		}
	};

	/** 线程池 */
	private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(

	CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			WORK_QUEUE, THREAD_FACTORY, HANDLER);

	/**
	 * 开始执行任务
	 * 
	 * @param work
	 *            要执行的runnable
	 * @param listener
	 *            文件任务监听，当任务被拒绝时回调
	 */
	public static void execute(DownloadTask task) {
		EXECUTOR.execute(task);
	}
}
