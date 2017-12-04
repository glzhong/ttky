package com.tiantiankuyin.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.utils.LrcUtils;

/**
 * 歌词对象类
 * 
 * @author Erica
 * */
public class Lyric implements Serializable {

	private static final long serialVersionUID = -1118487756786237874L;

	/** Sentence对象的集合 */
	public List<Sentence> list = new ArrayList<Sentence>();
	/** 是否初始化完成 */
	private boolean initDone;
	/** 歌词文件对象 */
	private transient File file;
	/** 进入标志位 */
	private boolean enabled = true;
	/** 读取的偏移量 */
	private int mOffset;
	/** 新的偏移量 */
	private int newOffset;
	/** 关键字符排除 */
	private static final Pattern pattern = Pattern
			.compile("(?<=\\[).*?(?=\\])");

	public Lyric(File file) {
		this.file = file;
		init(file);
		initDone = true;
		newOffset = mOffset;
	}

	public Lyric(String lyric) {
		this.init(lyric);
		initDone = true;
		newOffset = mOffset;
	}

	/**  */
	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	/**  */
	public File getLyricFile() {
		return file;
	}

	/**  */
	private void init(File file) {
		BufferedReader br = null;
		try {
			String encode = LrcUtils.getEncode(file);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), encode));
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			String lrcString = LrcUtils.unescapeHtml(sb.toString());//将里面的HTML转码
			parseLrc(lrcString);
			Lg.e("", "");
		} catch (Exception ex) {
			Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);

		} finally {
			try {
				br.close();
			} catch (Exception ex) {
				Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}

	/**  */
	private void init(String content) {
		if (content == null || content.trim().equals("")) {
			list.add(new Sentence("", Integer.MIN_VALUE, Integer.MAX_VALUE));
			return;
		}
		try {
			BufferedReader br = new BufferedReader(new StringReader(content));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				parseLine(temp.trim());
			}
			br.close();

			Collections.sort(list, new Comparator<Sentence>() {

				public int compare(Sentence o1, Sentence o2) {
					return (int) (o1.getFromTime() - o2.getFromTime());
				}
			});

			if (list.size() == 0) {
				list.add(new Sentence("", 0, Integer.MAX_VALUE));
				return;
			} else {
				Sentence first = list.get(0);
				list.add(0, new Sentence("", 0, first.getFromTime()));
			}

			int size = list.size();
			for (int i = 0; i < size; i++) {
				Sentence next = null;
				if (i + 1 < size) {
					next = list.get(i + 1);
				}
				Sentence now = list.get(i);
				if (next != null) {
					now.setToTime(next.getFromTime() - 1);
				}
			}
			if (list.size() == 1) {
				list.get(0).setToTime(Integer.MAX_VALUE);
			} else {
				Sentence last = list.get(list.size() - 1);
				last.setToTime(1000);
			}
		} catch (Exception ex) {
			Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 歌词解析
	 * @param srcLrc
	 */
	public void parseLrc(String srcLrc) {
		MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
		if (srcLrc.equals(""))
			return;
		final Pattern pattern = Pattern.compile("(?<=\\[).*?(?=\\])");
		Matcher matcher = pattern.matcher(srcLrc);
		List<String> temp = new ArrayList<String>();
		int lastIndex = -1;// 最后一个时间标签的下标
		int lastLength = -1;// 最后一个时间标签的长度
		while (matcher.find()) {
			String s = matcher.group();
			int index = srcLrc.indexOf("[" + s + "]");
			if (lastIndex != -1 && index - lastIndex > lastLength + 2) {
				// 如果大于上次的大小，则中间夹了别的内容在里面
				// 这个时候就要分段了
				String content = srcLrc.substring(lastIndex + lastLength + 2,
						index);
				
				//去掉歌词内容中的“<br>”
				if (content.contains("<br>")) {
					content = content.replace("<br>", "");
				}
				for (int i = 0; i < temp.size(); i++) {
					String str = temp.get(i);
					long t = parseTime(str);
					if (t != -1) {
						if (temp.size() == 1 || i == temp.size()-1 ) {
							list.add(new Sentence(content, t));
						} else {
							list.add(new Sentence("\n", t));
						}
					}
				}
				temp.clear();
			}
			temp.add(s);
			lastIndex = index;
			lastLength = s.length();
		}
		// 如果列表为空，则表示本行没有分析出任何标签
		if (temp.isEmpty()) {
			return;
		}
		Collections.sort(list, new Comparator<Sentence>() {

			public int compare(Sentence o1, Sentence o2) {
				return (int) (o1.getFromTime() - o2.getFromTime());
			}
		});
		// 处理第一句歌词的起始情况,无论怎么样,加上歌名做为第一句歌词,并把它的
		// 结尾为真正第一句歌词的开始
		if (list.size() == 0) {
			list.add(new Sentence(musicInfo.getTitle(), 0, Integer.MAX_VALUE));
			return;
		} else {
			Sentence first = list.get(0);
			list.add(0,
					new Sentence(musicInfo.getTitle(), 0, first.getFromTime()));
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Sentence next = null;
			if (i + 1 < size) {
				next = list.get(i + 1);
			}
			Sentence now = list.get(i);
			if (next != null) {
				now.setToTime(next.getFromTime() - 1);
			}
		}
		// 如果就是没有怎么办,那就只显示一句歌名了
		if (list.size() == 1) {
			list.get(0).setToTime(Integer.MAX_VALUE);
		} else {
			Sentence last = list.get(list.size() - 1);
			last.setToTime(musicInfo == null ? Integer.MAX_VALUE : musicInfo
					.getDuration()  + 1l);
			list.set(list.size()-1, last);
		}
	}

	/**  */
	private int parseOffset(String str) {
		String[] ss = str.split("\\:");
		if (ss.length == 2) {
			if (ss[0].equalsIgnoreCase("offset")) {
				int os = Integer.parseInt(ss[1]);
				return os;
			} else {
				return Integer.MAX_VALUE;
			}
		} else {
			return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * 把如00:00.00这样的字符串转化成 毫秒数的时间，比如 01:10.34就是一分钟加上10秒再加上340毫秒 也就是返回70340毫秒
	 * 
	 * @param time
	 *            字符串的时间
	 * @return 此时间表示的毫秒
	 */
	private long parseTime(String time) {
		String[] ss = time.split("\\:|\\.");
		// 如果 是两位以后，就非法了
		if (ss.length < 2) {
			return -1;
		} else if (ss.length == 2) {// 如果正好两位，就算分秒
			try {
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				if (min < 0 || sec < 0 || sec >= 60) {
					throw new RuntimeException("数字不合法!");
				}
				return (min * 60 + sec) * 1000L;
			} catch (Exception exe) {
				return -1;
			}
		} else if (ss.length == 3) {// 如果正好三位，就算分秒，十毫秒
			try {
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				int mm = Integer.parseInt(ss[2]);
				if (min < 0 || sec < 0 || sec >= 60 || mm < 0 || mm > 99) {
					throw new RuntimeException("数字不合法!");
				}
				return (min * 60 + sec) * 1000L + mm * 10;
			} catch (Exception exe) {
				return -1;
			}
		} else {// 否则也非法
			return -1;
		}
	}

	/**  */
	private void parseLine(String line) {
		if (line.equals("")) {
			return;
		}
		Matcher matcher = pattern.matcher(line);
		List<String> temp = new ArrayList<String>();
		int lastIndex = -1;
		int lastLength = -1;
		while (matcher.find()) {
			String s = matcher.group();
			int index = line.indexOf("[" + s + "]");
			if (lastIndex != -1 && index - lastIndex > lastLength + 2) {
				String content = line.substring(lastIndex + lastLength + 2,
						index);
				for (String str : temp) {
					long t = parseTime(str);
					if (t != -1) {
						list.add(new Sentence(content, t));
					}
				}
				temp.clear();
			}
			temp.add(s);
			lastIndex = index;
			lastLength = s.length();
		}

		if (temp.isEmpty()) {
			return;
		}
		try {
			int length = lastLength + 2 + lastIndex;
			String content = line.substring(length > line.length() ? line
					.length() : length);
			// if (Config.getConfig().isCutBlankChars()) {
			// content = content.trim();
			// }

			if (content.equals("") && mOffset == 0) {
				for (String s : temp) {
					int of = parseOffset(s);
					if (of != Integer.MAX_VALUE) {
						mOffset = of;
						break;
					}
				}
				return;
			}
			for (String s : temp) {
				long t = parseTime(s);
				if (t != -1) {
					list.add(new Sentence(content, t));
				}
			}
		} catch (Exception exe) {
		}
	}

	/**  */
	/*private long parseTime(String time) {
		String[] ss = time.split("\\:|\\.");
		if (ss.length < 2) {
			return -1;
		} else if (ss.length == 2) {
			try {
				if (mOffset == 0 && ss[0].equalsIgnoreCase("offset")) {
					mOffset = Integer.parseInt(ss[1]);
					return -1;
				}
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				if (min < 0 || sec < 0 || sec >= 60) {
					throw new RuntimeException("");
				}
				return (min * 60 + sec) * 1000L;
			} catch (Exception exe) {
				return -1;
			}
		} else if (ss.length == 3) {
			try {
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				int mm = Integer.parseInt(ss[2]);
				if (min < 0 || sec < 0 || sec >= 60 || mm < 0 || mm > 99) {
					throw new RuntimeException("");
				}
				return (min * 60 + sec) * 1000L + mm * 10;
			} catch (Exception exe) {
				return -1;
			}
		} else {
			return -1;
		}
	}*/

	/**
	 * 是否初始化完成
	 * 
	 * @return 操作完成与否
	 * */
	public boolean isInitDone() {
		return initDone;
	}

	/**
	 * 是否为新歌词对象索引标志位
	 * 
	 * @return 是则返回标志index否查询失败
	 * */
	public int getNowSentenceIndex(long t) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isInTime(t - mOffset)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 是否可以移动
	 * 
	 * @return 移动与否
	 * */
	public boolean canMove() {
		return list.size() > 1 && enabled;
	}

	/**
	 * 设置偏移量
	 * 
	 * @param offset
	 */
	public void setOffset(int offset) {
		newOffset = offset;
	}

	/**
	 * 获取偏移量
	 * 
	 * @return
	 */
	public int getOffset() {
		return newOffset;
	}

	/**
	 * 保存变更偏移量
	 * 
	 * @param offset
	 *            int
	 * @return 操作成功与否
	 */
	public boolean saveChangeOffset(int offset) {
		if (offset == mOffset) {
			return true;
		}
		BufferedReader br = null;
		File newFile = new File(file.getAbsolutePath() + ".bak");
		FileOutputStream fos;
		try {
			if (newFile.exists()) {
				newFile.delete();
			}
			if (!newFile.createNewFile()) {
				return false;
			}
			fos = new FileOutputStream(newFile);

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			String temp = null;
			boolean bCheck = true;
			while ((temp = br.readLine()) != null) {
				if (bCheck && temp.length() > 8) {
					if (Character.isDigit(temp.charAt(1))
							&& Character.isDigit(temp.charAt(2))) {
						fos.write(("[offset:" + offset + "]\n")
								.getBytes("UTF-8"));
						bCheck = false;
					} else if (temp.toLowerCase().indexOf("offset") > 0) {
						fos.write(("[offset:" + offset + "]\n")
								.getBytes("UTF-8"));
						bCheck = false;
						continue;
					}
				}

				fos.write((temp + "\n").getBytes("UTF-8"));
			}
			fos.flush();
			br.close();
			fos.close();

			file.delete();
			newFile.renameTo(file);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

}
