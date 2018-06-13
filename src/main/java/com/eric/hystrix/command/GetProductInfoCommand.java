package com.eric.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.eric.hystrix.model.ProductInfo;
import com.eric.hystrix.utils.HttpClientUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 调用商品服务获取商品信息的command
 * 使用线程池(Thread)的隔离方法,满足99%的业务场景
 * @author pxl
 *
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo>{
	
	private Long productId;
	
	public GetProductInfoCommand(Long productId) {
		super(Setter
				//CommandGroup(必选项),通常一个服务设置同一个group,用于统一监控
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"))
				//CommandKey(非必填),默认为类名
				.andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfoCommand"))
				//ThreadPoolKey(非必填),默认一个group使用同一个线程池,指定后每个command使用不同的线程池
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfoThreadPool"))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						.withCoreSize(10)//设置线程池大小,默认为10
						.withMaxQueueSize(5))//设置队列大小,默认为5,线程池满了后会放入此队列中,若队列满则会failback
				);
		this.productId = productId;
	}

	@Override
	protected ProductInfo run() throws Exception {
		String url = "http://localhost:8081/product/getProductInfo?productId=" + productId;
		String productJson = HttpClientUtils.sendGetRequest(url);
		return JSONObject.parseObject(productJson, ProductInfo.class);
	}

}
