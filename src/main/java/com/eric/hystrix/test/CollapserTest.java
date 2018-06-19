package com.eric.hystrix.test;

import com.eric.hystrix.utils.HttpClientUtils;

/**
 * 请求合并的测试
 * @author pxl
 *
 */
public class CollapserTest {
	
	public static void main(String[] args) {
		String url = "http://localhost:8081/cache/getProductInfosRequestCollapser?productIds=1,2,3,4,2,1,4,3,5";
		HttpClientUtils.sendGetRequest(url);
	}
}
