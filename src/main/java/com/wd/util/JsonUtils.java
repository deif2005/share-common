package com.wd.util;

import com.google.gson.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/***
 * 针对json 进行解析的工具类
 * @author
 */
public class JsonUtils {

	private static Gson gson = null;

	static {
		if (gson == null) {
			gson = new Gson();
		}
	}

	private JsonUtils() {
	
	}

	/**
	 * 将对象转换成json格式
	 * 
	 * @param ts
	 * @return
	 */
	public static String objectToJson(Object ts) {
		String jsonStr = null;
		if (gson != null) {
			jsonStr = gson.toJson(ts);
		}
		return jsonStr;
	}

	/**
	 * 将对象转换成json格式(并自定义日期格式)
	 * 
	 * @param ts
	 * @return
	 */
	public static String objectToJsonDateSerializer(Object ts,
			final String dateformat) {
		String jsonStr = null;
		gson = new GsonBuilder()
				.registerTypeHierarchyAdapter(Date.class,
						new JsonSerializer<Date>() {
							@Override
							public JsonElement serialize(Date src,
									Type typeOfSrc,
									JsonSerializationContext context) {
								SimpleDateFormat format = new SimpleDateFormat(
										dateformat);
								return new JsonPrimitive(format.format(src));
							}

							
						}).setDateFormat(dateformat).create();
		if (gson != null) {
			jsonStr = gson.toJson(ts);
		}
		return jsonStr;
	}

	/**
	 * 将json格式转换成list对象
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<?> jsonToList(String jsonStr) {
		List<?> objList = null;
		if (gson != null) {
			Type type = new com.google.gson.reflect.TypeToken<List<?>>(){}.getType();
			objList = gson.fromJson(jsonStr, type);
		}
		return objList;
	}

	/**
	 * 将json格式转换成list对象，并准确指定类型
	 * @param jsonStr
	 * @param type
	 * @return
	 */
	public static List<?> jsonToList(String jsonStr, Type type) {
		List<?> objList = null;
		if (gson != null) {
			objList = gson.fromJson(jsonStr, type);
		}
		return objList;
	}

	/**
	 * 将json格式转换成map对象
	 *
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, String> jsonToMap(String jsonStr) {
		Map<String, String> objMap = null;
		if (gson != null) {
			Type type = new com.google.gson.reflect.TypeToken<Map<String, String>>() {
			}.getType();
			objMap = gson.fromJson(jsonStr, type);
		}
		return objMap;
	}

	/**
	 * 将json转换成bean对象
	 *
	 * @param jsonStr
	 * @return
	 */
	public static Object jsonToBean(String jsonStr, Class<?> cl) {
		Object obj = null;
		if (gson != null) {
			obj = gson.fromJson(jsonStr, cl);
		}
		return obj;
	}

	/**
	 * 将json转换成bean对象
	 * @param jsonStr
	 * @return
	 */
	public static Object jsonToBean(String jsonStr){
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		Object object = JSONObject.toBean(jsonObject);
		return object;
	}

	/**
	 *
	 * @param jsonStr
	 * @param cl
	 * @return
	 */
	public static List<?> jsonToList(String jsonStr,Class<?> cl){
		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		List<?> list = (List<?>) JSONArray.toCollection(jsonArray,cl);
		return list;
	}

	/**
	 * 将json转换成bean对象
	 *
	 * @param jsonStr
	 * @param cl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T jsonToBeanDateSerializer(String jsonStr, Class<T> cl,
			final String pattern) {
		Object obj = null;
		gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
					@Override
					public Date deserialize(JsonElement json, Type typeOfT,
							JsonDeserializationContext context)
							throws JsonParseException {
						SimpleDateFormat format = new SimpleDateFormat(pattern);
						String dateStr = json.getAsString();
						try {
							return format.parse(dateStr);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return null;
					}

					
					
				}).setDateFormat(pattern).create();
		if (gson != null) {
			obj = gson.fromJson(jsonStr, cl);
		}
		return (T) obj;
	}

	/**
	 * 根据key获取对应value值
	 * 
	 * @param jsonStr
	 * @param key
	 * @return
	 */
	public static Object getJsonValue(String jsonStr, String key) {
		Object rulsObj = null;
		Map<?, ?> rulsMap = jsonToMap(jsonStr);
		if (rulsMap != null && rulsMap.size() > 0) {
			rulsObj = rulsMap.get(key);
		}
		return rulsObj;
	}

	/**
	 * 验证设备平台操作是否成功 错就抛出异常
	 *
	 * @param json       传入JSON字符串
	 */
	public static net.sf.json.JSONObject verifyCode(String json) {
		String data = json.toLowerCase();
		String code ="";
		String result = "";
		String msg="";
		net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(data);
		if (jsonObject.containsKey("code"))
			code = jsonObject.getString("code");

		if (jsonObject.containsKey("result"))
			result = jsonObject.getString("result");

		if (jsonObject.containsKey("msg"))
			msg = jsonObject.getString("msg");

//		if(StringUtils.isBlank(result)){
//			result = "失败";
//		}
		if(!"0".equals(code)){
			throw new RuntimeException(code+"&&"+msg);
		}
		return jsonObject;
	}



}
