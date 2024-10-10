package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 *
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class PublicUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
	    //允许出现不识别的字段
	    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
    }

	public static <T> T objectToObject(Object params,Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		if(params ==null) {return null;}
		return  objectMapper.readValue(objectMapper.writeValueAsBytes(params),clazz);
	}

	public static <T> T mapToObject(Map<String,Object> params,Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		return  objectMapper.readValue(objectMapper.writeValueAsBytes(params),clazz);
	}

	public static <T> T jsonStrToObject(String jsonStr, Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		return  objectMapper.readValue(jsonStr, clazz);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> strToMap(String str) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str, Map.class);
	}

	public static <T> T strToObject(String str,Class<T> classzz) throws  IOException{
		if(str ==null){
			return null;
		}

		return objectMapper.readValue(str,classzz);
	}


    public static <T> T jsonStrToObjectWithOutNull(String jsonStr, Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException {
        JSONObject origin = JSONObject.parseObject(jsonStr);
        JSONObject change = new JSONObject();
        for (String key : origin.keySet()) {
            if (null != origin.get(key)) {
                change.put(key, origin.get(key));
            }
        }
        return objectMapper.readValue(change.toJSONString(), clazz);
    }


    @SuppressWarnings("unchecked")
	public static Map<String,Object> objectToMap(Object obj) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{

		return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), Map.class);
	}

	public static String objToString(Object obj) throws IOException {
		return objectMapper.writeValueAsString(obj);
	}


	public static boolean count(int index,int multiples){
		return index%multiples==0;
	}

	public static Properties stringToProperties(String str) throws IOException{
	   Properties properties = new Properties();
	   properties.load(new ByteArrayInputStream(str.getBytes("UTF-8")));
	   return properties;
	}

	public static Properties trimProperties(Properties properties) {
		try {
			Iterator<Object> iterator = properties.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				properties.put(key, properties.getProperty(key).trim());
			}
		} catch (Exception ignoreException) {
		}
		return properties;
	}

	public static String propertiesToString(Properties properties) throws IOException{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		properties.store(byteArrayOutputStream, StringUtils.EMPTY);
		return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
	}

	public static boolean isJavaBaseType(Class<?> clazz){
		if(Integer.class.equals(clazz) || int.class.equals(clazz)){
			return true;
		}
		if(Long.class.equals(clazz) || long.class.equals(clazz)){
			return true;
		}
		if(Double.class.equals(clazz) || double.class.equals(clazz)){
			return true;
		}
		if(Float.class.equals(clazz) || float.class.equals(clazz)){
			return true;
		}
		if(Byte.class.equals(clazz) || byte.class.equals(clazz)){
			return true;
		}
		if(Short.class.equals(clazz) || short.class.equals(clazz)){
			return true;
		}
		if(clazz.equals(Boolean.class)||boolean.class.equals(clazz)){
			return true;
		}
		if(String.class.equals(clazz)){
			return true;
		}
		return false;
	}


	public static Object classConvter(Class<?> clazz,Object obj){
		if(obj ==null) {return null;}
		if(clazz.equals(Integer.class)||int.class.equals(clazz)){
			obj = Integer.parseInt(obj.toString());
		}else if(clazz.equals(Long.class)|| long.class.equals(clazz)){
			obj = Long.parseLong(obj.toString());
		}else if(clazz.equals(Double.class)|| double.class.equals(clazz)){
			obj = Double.parseDouble(obj.toString());
		}else if(clazz.equals(Float.class)|| float.class.equals(clazz)){
			obj = Float.parseFloat(obj.toString());
		}else if(clazz.equals(Byte.class)|| byte.class.equals(clazz)){
			obj = Byte.parseByte(obj.toString());
		}else if(clazz.equals(Short.class)|| short.class.equals(clazz)){
			obj = Short.parseShort(obj.toString());
		}else if(clazz.equals(Boolean.class)||boolean.class.equals(clazz)){
			obj = Boolean.parseBoolean(obj.toString());
		}else if(clazz.equals(String.class)){
			obj = obj.toString();
		}
		return obj;
	}

	public static String objectToStr(Object object) throws  IOException{
		if(object ==null){
			return null;
		}

		return objectMapper.writeValueAsString(object);
	}

	/**
	 * map列表转化为实体对象列表
	 * @param maps
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * @throws IOException
	 */
	public static <T> List<T> mapListToVoList(List<Map<String,Object>> maps,Class<T> clazz) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException{
		List<T> dependencyTaskVos = new LinkedList<>();
		if (maps != null && maps.size() != 0){
			for (Map<String, Object> map : maps) {
				dependencyTaskVos.add(objectMapper.readValue(objectMapper.writeValueAsBytes(map),clazz));
			}
		}
		return dependencyTaskVos;
	}

	public static String propToJson(String taskParams) {
		Properties properties = new Properties();
		if (StringUtils.isBlank(taskParams)) {
			return null;
		}
		try {
			properties.load(new ByteArrayInputStream(taskParams.replace("hiveconf:", "").getBytes(StandardCharsets.UTF_8)));
		} catch (IOException ignore) {}
		JSONObject result = new JSONObject();
		properties.forEach((key, value) -> result.put(key.toString(), value));
		return result.toJSONString();
	}


	public static void removeRepeatJSON(JSONObject originJSON, JSONObject newJSON) {
		if (originJSON.equals(newJSON)) {
			originJSON.clear();
			newJSON.clear();
		}
		Iterator<String> iterator = originJSON.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object o = originJSON.get(key);
			if (o instanceof JSONObject) {
				removeRepeatJSON((JSONObject) o, (JSONObject) newJSON.get(key));
			} else if (o instanceof JSONArray) {
				//ignore
			} else {
				if (newJSON.containsKey(key) && newJSON.get(key).equals(o)) {
					newJSON.remove(key);
					iterator.remove();
				}
			}
		}
	}

}
