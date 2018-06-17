package com.eric.hystrix.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 城市缓存
 * @author pxl
 *
 */
public class CityCache {
	
	private static Map<Long, String> CITY_MAP = new HashMap<Long, String>();
	
	static {
		CITY_MAP.put(1L, "北京");
		CITY_MAP.put(2L, "上海");
	}
	
	public static String getCityName(Long cityId) {
		return CITY_MAP.get(cityId);
	}
}
