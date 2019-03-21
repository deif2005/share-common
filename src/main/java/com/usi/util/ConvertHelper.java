package com.wd.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConvertHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ConvertHelper.class);

  private static Map<Class<?>, Map<String, Method>> s_map = new HashMap();

  public static <D> D convert(Object src, Class<D> clzDst) {
    if (src == null) {
      return null;
    }
    assert (clzDst != null);
    Object dst = null;
    try {
      dst = clzDst.newInstance();
    } catch (InstantiationException e) {
      LOGGER.error("Unexpected exception", e);
      throw new RuntimeException("Unexpected exception", e);
    } catch (IllegalAccessException e) {
      LOGGER.error("Unexpected exception", e);
      throw new RuntimeException("Unexpected exception", e);
    }

    BeanUtils.copyProperties(src, dst);

    return (D) dst;
  }

  private static Map<String, Method> getMethodLookupMap(Class<?> clz) {
    synchronized (s_map) {
      Map methodMap = (Map)s_map.get(clz);
      if (methodMap == null) {
        methodMap = new HashMap();

        for (Method method : clz.getMethods()) {
          String methodName = method.getName();
          if ((methodName.startsWith("set")) && 
            (method.getParameterTypes() != null) && (method.getParameterTypes().length == 1)) {
            method.setAccessible(true);
            methodMap.put(methodName, method);
          }
        }
      }

      return methodMap;
    }
  }

  private static Method findSetter(Class<?> clz, String methodName)
  {
    Map methodMap = getMethodLookupMap(clz);
    return (Method)methodMap.get(methodName);
  }
}