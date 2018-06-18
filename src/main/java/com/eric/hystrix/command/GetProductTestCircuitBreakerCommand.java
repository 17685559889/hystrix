package com.eric.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.eric.hystrix.model.ProductInfo;
import com.eric.hystrix.utils.HttpClientUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * 测试短路器的command
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
						.withCircuitBreakerRequestVolumeThreshold(30)//一个时间窗口所需的流量
						.withCircuitBreakerErrorThresholdPercentage(40)//打开断路器的失败比例
						.withCircuitBreakerSleepWindowInMilliseconds(3000)));//恢复断路器至打开状态的时间
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
