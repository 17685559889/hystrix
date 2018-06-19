package com.eric.hystrix.test;

import com.eric.hystrix.utils.HttpClientUtils;

/**
 * request cache 测试
 * @author pxl
 *
 */
public class RequestCacheTest {
	
	public static void main(String[] args) {
		
		String url = "http://localhost:8081/cache/getProductInfosWithRequestCache?productIds=1,2,3,4,2,1,4,3,5";
		HttpClientUtils.sendGetRequest(url);
		
	}
}
