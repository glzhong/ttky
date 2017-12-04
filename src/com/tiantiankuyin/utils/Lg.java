package com.tiantiankuyin.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

import com.tiantiankuyin.para.Constant;

public class Lg {
	/** 日志Tag */
	public static final String TAG = "easou";  
	/** 是否打印日志 */
	public static boolean isDebug = false;  

	private static LinkedList<LogContent> logs = new LinkedList<LogContent>();
	/**
	 * 是否正在缓冲，以便一次向日志文件中写入更多日志，减少IO操作
	 */
	private static boolean isWritingLog = false;

	/**
	 * 锁logs
	 */
	private final static byte[] LOCK_LOGS = new byte[0];
	/**
	 * 同一时刻，对日志文件只允许一IO操作
	 */
	private final static byte[] LOCK_LOG_FILE_WRITE = new byte[0];

	private static final int V = 0, D = 1, I = 2, W = 3, E = 4;

	public Lg() {
	}

	/**
	 * 执行是否将目录保存的策略判断
	 * 
	 * @param tag
	 * @param level
	 * @param msg
	 * @return
	 */
	private static boolean isNeedSaveLogToFile(String tag, int level, String msg) {
		// 当日志级别为E时，写入日志文件
		//if (level == E || level == W || level == I || level == D) {
		if(level == E){
			return true;
		}
		return false;
	}

	public static void v(String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.v(Lg.TAG, t);
			writeToLogFile(Lg.TAG, V, t);
		}
	}

	public static void v(String msg, Throwable thr) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.v(Lg.TAG, t, thr);
			writeToLogFile(Lg.TAG, V, t);
		}
	}

	public static void v(String tag, String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.v(tag, t);
			writeToLogFile(tag, V, t);
		}
	}

	public static void d(String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.d(Lg.TAG, t);
			writeToLogFile(Lg.TAG, D, t);
		}
	}

	public static void d(String tag, String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.d(tag, t);
			writeToLogFile(tag, D, t);
		}
	}

	public static void d(String msg, Throwable thr) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.d(Lg.TAG, t, thr);
			writeToLogFile(Lg.TAG, D, t);
		}
	}

	public static void i(String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.i(Lg.TAG, t);
			writeToLogFile(Lg.TAG, I, t);
		}
	}

	public static void i(String tag, String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.i(tag, t);
			writeToLogFile(tag, I, t);
		}
	}

	public static void i(String msg, Throwable thr) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.i(Lg.TAG, t, thr);
			writeToLogFile(Lg.TAG, I, t);
		}
	}

	public static void w(String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.w(Lg.TAG, t);
			writeToLogFile(Lg.TAG, W, t);
		}
	}

	public static void w(String tag, String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.w(tag, t);
			writeToLogFile(tag, W, t);
		}
	}

	public static void w(String msg, Throwable thr) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.w(Lg.TAG, t, thr);
			writeToLogFile(Lg.TAG, W, t);
		}
	}

	public static void e(String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.e(Lg.TAG, t);
			writeToLogFile(Lg.TAG, E, t);
		}
	}

	public static void e(String tag, String msg) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.e(tag, t);
			writeToLogFile(tag, E, t);
		}
	}

	public static void e(String msg, Throwable thr) {
		if (Lg.isDebug) {
			String t = buildMessage(msg);
			android.util.Log.e(Lg.TAG, t, thr);
			writeToLogFile(Lg.TAG, E, t);
		}
	}

	protected static String buildMessage(String msg) {
		StackTraceElement caller = new Throwable().fillInStackTrace()
				.getStackTrace()[2];

		return new StringBuilder().append(caller.getClassName()).append(".")
				.append(caller.getMethodName()).append("(): ").append(msg)
				.toString();
	}

	/**
	 * 异步将日志写入sdcard
	 */
	private static void writeToLogFile(String tag, int level, String msg) {
		if (isNeedSaveLogToFile(tag, level, msg)) {
			synchronized (LOCK_LOGS) {
				logs.add(new LogContent(tag, level, msg));
			}
			writeToLogFileAsynch();
		}
	}

	private static void writeToLogFileAsynch() {
		new Thread() {
			@Override
			public void run() {
				if (!isWritingLog) {
					synchronized (LOCK_LOG_FILE_WRITE) {
						if (isWritingLog) {
							return;
						} else {
							isWritingLog = true;
						}
						synchronized (LOCK_LOGS) {
							if (logs.size() == 0) {
								isWritingLog = false;
								return;
							}
						}

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						FileWriter fw = null;
						try {
							File dir = new File(
									Constant.SdcardPath.LOG_SAVEPATH);
							if (!dir.exists() && !dir.mkdirs()) {
								return;
							}
							File logFile = new File(dir, "Log"
									+ Format.getDate() + ".txt");
							if (!logFile.exists()) {
								logFile.createNewFile();
							}
							fw = new FileWriter(logFile, true);
							fw.write(getAllLog());
							fw.flush();
							fw.close();
							fw = null;
						} catch (IOException e) {
							e.printStackTrace();
							if (fw != null) {
								try {
									fw.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}

						}
						isWritingLog = false;
					}
					// 循环调用自己，写保证后添加的日志也写入了文本文件
					writeToLogFileAsynch();
				}
			}
		}.start();

	}

	private static String getAllLog() {
		synchronized (LOCK_LOGS) {
			if (logs.size() == 0) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			while (logs.size() > 1) {
				sb.append(logs.remove(0).toString() + "\n");
			}

			return sb.toString();
		}
	}

	/**
	 * 日志bean
	 * 
	 * @author looming
	 * 
	 */
	static class LogContent {
		String tag;
		int level;
		String msg;
		Date datetime = new Date();

		public LogContent(String tag, int level, String msg) {
			this.tag = tag;
			this.level = level;
			this.msg = msg;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		@Override
		public String toString() {
			return "[" + Format.getDatetimeMillisecond(datetime) + "-" + tag
					+ "-" + level + "] " + msg;
		}

	}
}
