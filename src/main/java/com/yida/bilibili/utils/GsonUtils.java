package com.yida.bilibili.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yida
 * @package com.yida.bilibili.utils
 * @date 2022-09-10 15:59:
 * @description GSON工具类
 */
public class GsonUtils {
	private static Gson gson;
	private static GsonUtils gsonUtils;

	private GsonUtils() {
		gson = new Gson();
	}

	public static GsonUtils getInstance() {
		if (gsonUtils == null) {
			synchronized (GsonUtils.class) {
				if (gsonUtils == null) {
					gsonUtils = new GsonUtils();
				}
			}
		}
		return gsonUtils;
	}

	/**
	 * 将json转化为对应的实体对象
	 * new TypeToken<HashMap<String, Object>>(){}.getType()
	 */
	private <T> T fromJson(String json, Type type) {
		return gson.fromJson(json, type);
	}

	/**
	 * 将json字符串转化成实体对象
	 *
	 * @param json json字符串
	 * @param <T>  目标对象类型
	 * @return 目标对象实例
	 */
	public <T> T toObject(String json, Class<T> clz) {
		return fromJson(json, clz);
	}

	/**
	 * toJson
	 *
	 * @param src 对象
	 * @return String
	 */
	public static String toJson(Object src) {
		return gson.toJson(src);
	}

	/**
	 * 获取JsonObject
	 * @param json
	 * @return
	 */
	public static JsonObject parseJson(String json){
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = parser.parse(json).getAsJsonObject();
		return jsonObj;
	}

	/**
	 * 根据json字符串返回Map对象
	 * @param json
	 * @return
	 */
	public static Map<String, Object> toMap(String json){
		return toMap(parseJson(json));
	}

	/**
	 * 将JSONObjec对象转换成Map-List集合
	 * @param json
	 * @return
	 */
	public static Map<String, Object> toMap(JsonObject json){
		Map<String, Object> map = new HashMap<String, Object>();
		Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
		for (Iterator<Map.Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
			Map.Entry<String, JsonElement> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof JsonArray) {
				map.put((String) key, toList((JsonArray) value));
			} else if(value instanceof JsonObject) {
				map.put((String) key, toMap((JsonObject) value));
			} else {
				map.put((String) key, value);
			}
		}
		return map;
	}

	/**
	 * 将JSONArray对象转换成List集合
	 * @param json
	 * @return
	 */
	public static List<Object> toList(JsonArray json){
		List<Object> list = new ArrayList<Object>();
		for (int i=0; i < json.size(); i++){
			Object value = json.get(i);
			if(value instanceof JsonArray){
				list.add(toList((JsonArray) value));
			} else if(value instanceof JsonObject){
				list.add(toMap((JsonObject) value));
			} else{
				list.add(value);
			}
		}
		return list;
	}
}
