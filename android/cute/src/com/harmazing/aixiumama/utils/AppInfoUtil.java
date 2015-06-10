package com.harmazing.aixiumama.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONException;
import org.json.JSONObject;

public class AppInfoUtil {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] b) {
		// String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	/**
	 * 生成Md5
	 * 
	 * @param s 字符串
	 * @return 字符串的MD5值
	 */
	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}
	/**
	 * Json字符串转化成JSONObject
	 * @param str
	 * @return JSONObject对象
	 * @throws Exception
	 */
	public static JSONObject stringToJSONObject(String str) throws Exception {
		try {
			return new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}
}
