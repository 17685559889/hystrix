package com.eric.hystrix.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 品牌本地缓存
 * 用于模拟降级取用
 * @author pxl
 *
 */
public class BrandCache {
	
	private static Map<Long, String> brandMap = new HashMap<Long, String>();
	
	static {
		brandMap.put(1L, "Apple");
	}
	
	public static String getBrandName(Long brandId) {
		return brandMap.get(brandId);
	}
}
