package com.eric.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.eric.hystrix.model.ProductInfo;
import com.eric.hystrix.utils.HttpClientUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * 测试短路器的command
 * 达到条件后快速熔断
 * 断路器打开的条件:
 * 1.一个时间窗口期经过断路器的流量达到设定水平
 * 2.失败流量的比例达到设定水平
 * @author pxl
 *
 */
public class GetProductTestCircuitBreakerCommand extends HystrixCommand<ProductInfo> {

	private Long productId;
	
	public GetProductTestCircuitBreakerCommand(Long productId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductTestCircuitBreakerGroup"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						//熔断器在整个统计时间内是否开启的阀值，默认20。也就是10秒钟内至少请求20次，熔断器才发挥起作用
						.withCircuitBreakerRequestVolumeThreshold(30)
						//当出错率超过50%后熔断器启动,默认:50。
						.withCircuitBreakerErrorThresholdPercentage(40)
						//熔断器默认工作时间,默认:5秒.熔断器中断请求5秒后会关闭重试,如果请求仍然失败,继续打开熔断器5秒,如此循环
						.withCircuitBreakerSleepWindowInMilliseconds(3000)));
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
