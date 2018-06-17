package com.eric.hystrix.command;

import com.eric.hystrix.cache.CityCache;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;

/**
 * 调用本地缓存获取城市信息
 * 使用信号量(semaphore)的隔离方法,常用于比较耗时的本地方法,性能比线程池更高
 * @author pxl
 *
 */
public class GetCityNameCommand extends HystrixCommand<String> {
	
	private Long cityId;
	
	public GetCityNameCommand(Long cityId) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetCityNameGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("GetCityNameCommand"))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetCityNamePool"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
		        		.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)
		        		.withExecutionIsolationSemaphoreMaxConcurrentRequests(15))
			);
		this.cityId = cityId;
	}

	@Override
	protected String run() throws Exception {
		return CityCache.getCityName(cityId);
	}

}
