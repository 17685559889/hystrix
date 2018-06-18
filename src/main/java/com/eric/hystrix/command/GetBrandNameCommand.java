package com.eric.hystrix.command;

import com.eric.hystrix.cache.BrandCache;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;

/**
 * 获取品牌名称command
 * 用于演示调用失败进行降级
 * @author pxl
 *
 */
public class GetBrandNameCommand extends HystrixCommand<String> {
	
	private Long brandId;
	
	public GetBrandNameCommand(Long brandId) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetBrandNameGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("GetBrandNameCommand"))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetBrandNamePool"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
		        		.withFallbackIsolationSemaphoreMaxConcurrentRequests(10))//设置降级策略的最大并发数
			);
		this.brandId = brandId;
	}

	@Override
	protected String run() throws Exception {
		//模拟报错进行降级
		throw new Exception();
	}
	
	@Override
	protected String getFallback() {
		System.out.println("brandId:" + brandId + "执行降级,从本地缓存中获取品牌名称!");
		return BrandCache.getBrandName(brandId);
	}

}
