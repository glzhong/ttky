package com.tiantiankuyin.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringEscapeUtils;

import com.tiantiankuyin.bean.OlLrcItem;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * 对歌词处理的公共方法。
 * 
 * @author Perry
 */
public class LrcUtils {
	/**
	 * <p>
	 * Checks if a CharSequence is empty ("") or null.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 * 
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the
	 * CharSequence. That functionality is available in isBlank().
	 * </p>
	 * 
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is empty or null
	 * @since 3.0 Changed signature from isEmpty(String) to
	 *        isEmpty(CharSequence)
	 */
	private static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * <p>
	 * Checks if a String is whitespace, empty ("") or null.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("bob")     = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 * @since 2.0
	 */
	private static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Checks whether the character is ASCII 7 bit alphabetic.
	 * </p>
	 * 
	 * <pre>
	 *   CharUtils.isAsciiAlpha('a')  = true
	 *   CharUtils.isAsciiAlpha('A')  = true
	 *   CharUtils.isAsciiAlpha('3')  = false
	 *   CharUtils.isAsciiAlpha('-')  = false
	 *   CharUtils.isAsciiAlpha('\n') = false
	 *   CharUtils.isAsciiAlpha('&copy;') = false
	 * </pre>
	 * 
	 * @param ch
	 *            the character to check
	 * @return true if between 65 and 90 or 97 and 122 inclusive
	 */
	private static boolean isAsciiAlpha(char ch) {
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
	}

	/**
	 * 判断是否是LRC歌词
	 * 
	 * @param content
	 * @return
	 * @author Snail
	 */
	public static boolean isLRC(String content) {
		if (isBlank(content))
			return false;
		String tmp = content;
		tmp = tmp.replaceAll(" +", "");
		tmp = tmp.replaceAll("(?i)<br\\s*/?>", "\n");
		if (tmp.length() < 50) {
			return false;
		}
		// System.out.println(countMatch(content, "[\\r\\n]?\\S+"));
		if (countMatch(tmp, "[\\r\\n]?\\S+") < 6) {
			return false;
		}
		return true;
	}

	private static int countMatch(String src, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(src);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}

	/**
	 * 判断字符c是否是中日韩字符
	 * 
	 * @param c
	 * @return
	 * @author Snail
	 */
	// GENERAL_PUNCTUATION 判断中文的“号 //
	// CJK_SYMBOLS_AND_PUNCTUATION 判断中文 的。号 /
	// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
	private static boolean isCJK(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_COMPATIBILITY// 1
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS// 1
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
				|| ub == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT// 1
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS// 1
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A// 1
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.HANGUL_SYLLABLES
				|| ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
				|| ub == Character.UnicodeBlock.HANGUL_JAMO
				|| ub == Character.UnicodeBlock.KATAKANA
				|| ub == Character.UnicodeBlock.HIRAGANA
				|| ub == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串str是否为乱码 <br>
	 * <b>乱码：非数字非英文非符号和非中日韩文的字符</b>
	 * 
	 * @param str
	 * @return
	 * @author Snail
	 */
	private static boolean isMessyCode(String str) {

		if (str != null) {
			str = str.replaceAll("\\s*|\t*|\r*|\n*", "");
			str = str.replaceAll("\\p{P}", "");

			char[] ch = str.trim().toCharArray();
			float chLength = ch.length;
			// System.out.println("chLength=" + chLength);
			float count = 0;
			for (int i = 0; i < ch.length; i++) {
				char c = ch[i];
				if (Character.isLetter(c)) { // 确定指定字符是否为字母或数字

					if (!isAsciiAlpha(c) && !isCJK(c)) {
						count = count + 1;
						// System.out.print(c);
					}
				}
			}
			// System.out.println("count=" + count);
			float result = count / chLength;
			if (result > 0.4) {
				return true;
			}

		}
		return false;
	}

	/**
	 * 统计字符串包含乱码字符的个数 <br>
	 * <b>乱码：非数字非英文非符号和非中日韩文的字符</b>
	 * 
	 * @param str
	 * @return
	 * @author Snail
	 */
	public static int countMessyCode(String str) {
		int count = 0;
		if (str != null) {
			str = str.replaceAll("\\s*|\t*|\r*|\n*", "");
			str = str.replaceAll("\\p{P}", "");

			char[] ch = str.trim().toCharArray();
			if (ch != null) {
				for (char c : ch) {
					if (Character.isLetter(c)) { // 确定指定字符是否为字母或数字
						if (!isAsciiAlpha(c) && !isCJK(c)) {
							count++;
						}
					}
				}
			}
		}
		return count;
	}

	public static boolean isMessyLrc(String str) {
		if (str != null) {
			int messyCodeCount = countMessyCode(str);
			boolean flag = messyCodeCount * 1.0 / str.length() > 0.6;
			return flag;
		} else {
			return true;
		}
	}

	/**
	 * 获取文件编码
	 * 
	 * @throws IOException
	 */
	public static String getEncode(File file) {
		InputStream in = null;
		String encoding = null;
		try {
			in = new FileInputStream(file);
			byte[] data = readStream(in);
			encoding = getEncode(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(encoding!=null && encoding.equalsIgnoreCase("GB18030")){
			encoding = "GB18030";
		}else {
			encoding = "UTF-8";
		}
		return encoding;
	}
	
	public static String getEncode(byte[] data){
		String encoding = null;
		CharsetDetector detector = new CharsetDetector();
		detector.setText(data);
		CharsetMatch match = detector.detect();
		encoding = match.getName();
		if(encoding!=null &&encoding.equalsIgnoreCase("GB18030")){
			encoding = "GB18030";
		}else {
			encoding = "UTF-8";
		}
		return encoding;
	}

	/**
	 * 读取流
	 * 
	 * @param inStream
	 * @return 字节数组
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream inStream) throws IOException {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
	
	/**
	 * 对一组歌词集合进行验证。
	 * 验证歌词是否为乱码，歌词是否符合格式。
	 * 最终返回一个可用的歌词下载地址。
	 * @param lrcLists
	 * @return 返回一个可用的歌词下载地址，有可能为null
	 * @author Perry
	 */
	public static String checkLrcUrl(List<OlLrcItem> lrcLists){
		String lrcUrl = null;
		for(OlLrcItem item: lrcLists) {
			String tempUrl = item.getLrcurl();
			try{
			URL url = new URL(tempUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(30000);
			conn.setDoInput(true);
			conn.setReadTimeout(30000);
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.connect();
			int responseCode = conn.getResponseCode();
			if(responseCode == 200 || responseCode == 206){
				InputStream responseStream = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(responseStream);
				bis.mark(2);
				byte[] header = new byte[2];
				int length = bis.read(header);
				bis.reset();
				// 判断是否是GZIP格式
				if (length != -1 && getShort(header) == 0x1f8b) {
					bis = new BufferedInputStream(new GZIPInputStream(bis));
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] bytes = new byte[1024];
				int len = -1;
				while ((len = bis.read(bytes)) != -1) {
					bos.write(bytes, 0, len);
				}
				byte[] lrcBytes = bos.toByteArray();
				if(bos!=null){
					bos.close();
					bos = null;
				}
				if(bis!=null){
					bis.close();
					bis = null;
				}
				String lrc = new String(lrcBytes,LrcUtils.getEncode(lrcBytes));
				if(LrcUtils.isLRC(lrc) && !LrcUtils.isMessyLrc(lrc)){
					lrcUrl = tempUrl;
					break;
				}
			}else {
				conn.disconnect();
				conn = null;
				lrcUrl = null;
				continue;
			}
			}catch (Exception e) {
				lrcUrl = null;
			}
		}
		return lrcUrl;
	}
	
	
	private static int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}
	
	/**
	 * HTML转码
	 * @param title
	 * @return
	 * @author Perry
	 */
	public static String unescapeHtml(String title) {
		title = title.replaceAll("&apos;", "'");
		return StringEscapeUtils.unescapeHtml(title);
	}
	
}
