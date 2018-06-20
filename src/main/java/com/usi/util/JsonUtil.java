package com.usi.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonUtil {
	
	
	public static ObjectNode getNoManagerResponse(){
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", 0);
        ObjectNode response=createObjectNode();
        response.replace("actions", createArrayNode());
        objectNode.replace("response", response);
        return objectNode;
    }
	
	public static ObjectNode getSizeAndListObjectNode(long count, List<?> list, Integer pageSize){
		ObjectNode objectNode=createObjectNode();
		objectNode.put("code", 1);
		ObjectNode response=createObjectNode();
		response.put("size", (count-1)/pageSize+1);
        response.replace("list", JsonUtil.objectToJsonNode(list));
        response.put("total", count);
        objectNode.replace("response", response);
		return objectNode;
	}
	
	public static ObjectNode getSizeAndListObjectNode(long count, ArrayNode list, Integer pageSize){
		ObjectNode objectNode=createObjectNode();
		objectNode.put("size", (count-1)/pageSize+1);
		ObjectNode response=createObjectNode();
		response.replace("list", JsonUtil.objectToJsonNode(list));
		objectNode.replace("response", response);
		return objectNode;
	}

	public static ObjectNode getListObjectNode(List<?> list){
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", 1);
        ObjectNode response=createObjectNode();
        response.replace("list", JsonUtil.objectToJsonNode(list));
        response.put("total", list == null?0:list.size());
        objectNode.replace("response", response);
        return objectNode;
    }

	public static ObjectNode getListObjectNodeNew(List<?> list){
		ObjectNode objectNode=createObjectNode();
		objectNode.put("code", 1);
		ObjectNode response=createObjectNode();
		response.replace("listCorp", JsonUtil.objectToJsonNode(list));
		response.put("total", list == null?0:list.size());
		objectNode.replace("response", response);
		return objectNode;
	}

	public static ObjectNode wrapStringResponse(String json) throws JsonProcessingException, IOException {
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", 1);
        ObjectNode response=createObjectNode();
        response.replace("response", JsonUtil.StringToJsonNode(json));
        return objectNode;
    }

	public static ObjectNode wrapObjectResponse(Object obj){
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", 1);
        objectNode.put("response", obj.toString());
        return objectNode;
    }

	public static ObjectNode wrapBooleanResponse(boolean obj){
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", 1);
        objectNode.put("response", obj);
        return objectNode;
    }

	public static ObjectNode wrapJsonNodeResponse(JsonNode obj){
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", 1);
        objectNode.replace("response", obj);
        return objectNode;
    }

	public static ObjectNode wrapReturn(boolean succ) {
        ObjectNode returnV = createObjectNode();
        if (succ)
            returnV.put("code", 1);
        else
            returnV.put("code", 0);
        return returnV;
    }
	
//	public static ObjectNode wrapCardResult(ResultInfo info) {
//        if (info.getCode() == 0){
//            return getObjectResponse(info);
//        } else {
//            return wrapFailStringResponse(0-info.getCode(), ObjectToString(info.getResult()));
//        }
//    }
//
//	public static ObjectNode wrapCardResult(PowerResultInfo info) {
//        if (info.getCode() == 0){
//            return wrapReturn(true);
//        } else {
//            return wrapFailStringResponse(0-info.getCode(), ObjectToString(info.getResult()));
//        }
//    }
	
	public static ObjectNode invalidUMResponse() {
        ObjectNode returnV = createObjectNode();
        returnV.put("code", -1);
        ObjectNode response = createObjectNode();
        response.put("reason", "invalid username or password");
        returnV.replace("response", response);
        return returnV;
    }

	public static ObjectNode wrapFailJsonNodeResponse(int failCode, JsonNode obj){
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", failCode);
        objectNode.replace("response", obj);
        return objectNode;
    }

	public static ObjectNode wrapFailStringResponse(int failCode, String reason){
        ObjectNode returnV=createObjectNode();
        returnV.put("code", failCode);
        returnV.put("reason", reason);
        return returnV;
    }

	public static ObjectNode getObjectResponse(Object obj) {
        ObjectNode objectNode = createObjectNode();
        objectNode.put("code", 1);
        objectNode.replace("response", JsonUtil.objectToJsonNode(obj));
        return objectNode;
    }
	
	/*public static ObjectNode getStatusObjectNode(Object obj,Integer status){
		ObjectNode objectNode=createObjectNode();
		objectNode.put("status", status);
		objectNode.put("object", JsonUtil.objectToJsonNode(obj));
		return objectNode;
	}*/
	
	
	public static <T> List<T> StringToObjectList(String str, Class<T> beanClz) {
        try {
            if (str == null || str.isEmpty() || beanClz == null) {
                // TODO
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JavaType javaType = getCollectionType(objectMapper, ArrayList.class, beanClz);
            return objectMapper.readValue(str, javaType); 
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);   
	}
	
	public static <T> T StringToObject(String str, TypeReference<T> type){
		try {
			if(str==null || str.isEmpty() || type==null){
				//TODO
				return null;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper.readValue(str,type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String ObjectToString(Object obj){
		try {
			if(obj==null){
				//TODO
				return null;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static JsonNode StringToJsonNode(String json) throws JsonProcessingException, IOException {
		if (StringUtils.isEmpty(json))
	        json = "{}";
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);
		return jsonNode;
	}
	
	
	public static JsonNode objectToJsonNode(Object obj){
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String objJson=objectMapper.writeValueAsString(obj);
			JsonNode jsonNode = objectMapper.readTree(objJson);
			return jsonNode;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static ObjectNode createObjectNode(){
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode ObjectNode = objectMapper.createObjectNode();
		return ObjectNode;
	}
	
	public static ArrayNode createArrayNode(){
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode arrayNode = objectMapper.createArrayNode();
		return arrayNode;
	}
	
	public static ObjectNode getMapObjectNode(Map map){
        ObjectNode objectNode=createObjectNode();
        objectNode.put("code", 1);
        ObjectNode response=createObjectNode();
        response.replace("map", JsonUtil.objectToJsonNode(map));
        objectNode.replace("response", response);
        return objectNode;
    }
	
	public static String ObjectToStringByReflect(Object obj, String extra){
		StringBuilder sbf = new StringBuilder();
	try {

		Class<?> clz = obj.getClass();
		Field[] fields = clz.getDeclaredFields();
		Field.setAccessible(fields, true);
		int size = fields.length;
		sbf.append("{");
		boolean bool = false;
		for (int i = 0; i < size; i++) {
			String fieldName=fields[i].getName();
			if("serialVersionUID".equals(fieldName)){
				continue;
			}
			sbf.append("\"");
			sbf.append(fieldName);
			sbf.append("\"");
			sbf.append(":");
			Object value = fields[i].get(obj);
			if ((fields[i].getType().equals(String.class) || fields[i].getType().equals(Date.class))
					&& value != null && !fieldName.equals("common_cate")) {
				bool = true;
				sbf.append("\"");
			} else {
				bool = false;
			}
			sbf.append(value);
			if (bool) {
				sbf.append("\"");
			}
			if (i != size - 1) {
				sbf.append(",");
			}

		}
		if(extra!=null || !"".equals(extra)){
			sbf.append(",");
			sbf.append(extra);
		}
		sbf.append("}");
	} catch (IllegalArgumentException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	}
	return sbf.toString();
	}
	
	public static String addChannelAttribute(String json, String childUrl, String url, String parament) throws JsonProcessingException, IOException {
		ArrayNode arrayNode=(ArrayNode)StringToJsonNode(json);
		
		if(arrayNode!=null){
			int size =arrayNode.size();
			ObjectNode objectNode=null;
			for(int i=0;i<size;i++){
				objectNode=(ObjectNode)arrayNode.get(i);
				objectNode.put("childUrl", childUrl+parament+"="+objectNode.get(parament));
				objectNode.put("url", url);
			}
			return arrayNode.toString();
		}
		return json;
	}
	
	public static String optString(ObjectNode node, String key, String defaultValue){
		JsonNode jsonNode = node.get(key);
		if (jsonNode == null)
			return defaultValue;
		else
			return jsonNode.asText();
	}

     public static Object nodeToObject(JsonNode node){
        if (node == null)
            return "";
        if (node instanceof DoubleNode){
            return node.asDouble();
        } else if (node instanceof TextNode){
            return node.asText();
        } else if (node instanceof BooleanNode){
            return node.asBoolean();
        } else if (node instanceof LongNode){
            return node.asLong();
        } else if (node instanceof IntNode){
            return node.asInt();
        }
        return node;
    }
    
    public static JsonNode getByJPath(JsonNode node, String jpath){
        if (!jpath.contains("."))
            return node.get(jpath);
        String[] paths = jpath.split("\\.");
        JsonNode temp = node;
        for (String path : paths){
            if (temp != null)
                temp = temp.get(path);
        }
        return temp;
    }

}


