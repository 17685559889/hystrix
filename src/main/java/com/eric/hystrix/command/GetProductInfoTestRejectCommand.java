package com.eric.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.eric.hystrix.model.ProductInfo;
import com.eric.hystrix.utils.HttpClientUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 测试接口限流的command
 * 
 * @author pxl
 *
 */
public class GetProductInfoTestRejectCommand extends HystrixCommand<ProductInfo> {

	private Long productId;

	public GetProductInfoTestRejectCommand(Long productId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("getProductInfoTestRejectGroup"))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						 //线程池大小
						.withCoreSize(10)
						 //最大等待队列大小
						.withMaxQueueSize(12)
						//队列中的等待数量超过该值则被限流进行降级,该值若大于最大等待队列,则使用最大等待队列的值
						.withQueueSizeRejectionThreshold(8)) 
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						//timeout时长
						.withExecutionTimeoutInMilliseconds(5000)
						//进行降级的最大并发数
						.withFallbackIsolationSemaphoreMaxConcurrentRequests(30)));
		this.productId = productId;
	}

	@Override
	protected ProductInfo run() throws Exception {
		if (productId < 0) {
			throw new IllegalArgumentException("非法的商品id");
		}
		String url = "http://localhost:8081/product/getProductInfo?productId=" + productId;
		String productJson = HttpClientUtils.sendGetRequest(url);
		return JSONObject.parseObject(productJson, ProductInfo.class);
	}

	@Override
	protected ProductInfo getFallback() {
		ProductInfo productInfo = new ProductInfo();
		productInfo.setId(productId);
		productInfo.setName("降级商品");
		return productInfo;
	}

}
