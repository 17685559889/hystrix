package com.eric.hystrix.test;

import com.eric.hystrix.utils.HttpClientUtils;

/**
 * 模拟断路器打开进行服务降级
 * @author pxl
 *
 */
public class HystrixCircuitBreakerTest {
	
	public static void main(String[] args) {
		String url = "http://localhost:8081/cache/getProductInfoTestCircuitBreaker?productId=";
		System.out.println("===============模拟成功15次================");
		for(int i = 1; i <= 15; i++) {
			HttpClientUtils.sendGetRequest(url + "1");
		}
		System.out.println("===============模拟失败20次================");
		for(int i = 1; i <= 20; i++) {
			HttpClientUtils.sendGetRequest(url + "-1");
		}
		try {
			System.out.println("============模拟经过3秒的时间窗口================");
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("============此时短路器处于打开状态,即使输入正确的id也会降级============");
		for(int i = 1; i <= 15; i++) {
			HttpClientUtils.sendGetRequest(url + "1");
		}
	}
}
