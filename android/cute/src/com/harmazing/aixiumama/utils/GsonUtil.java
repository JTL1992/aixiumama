package com.harmazing.aixiumama.utils;

import com.google.gson.Gson;

/**
 * 解析数据的工具类
 * @author Administrator
 *
 */

public class GsonUtil {
	/**
	 * 解析json数据,将json数据转换为bean对象
	 * @param content	需要解析的数据
	 * @param clazz		转化成bean对象类型
	 * @return	转化后的bean对象
	 */
	public static <T> T json2Bean(String content, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(content, clazz);
	}
}
