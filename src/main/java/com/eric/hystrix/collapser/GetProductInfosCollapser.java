package com.eric.hystrix.collapser;

import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.eric.hystrix.model.ProductInfo;
import com.eric.hystrix.utils.HttpClientUtils;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * 请求合并示例,获取商品信息
 * 与单独使用request cache相比，可以不必创建多个command
 * 即不用多次请求后台，减少了网络开销
 * @author pxl
 *
 */
public class GetProductInfosCollapser extends HystrixCollapser<List<ProductInfo>, ProductInfo, Long> {

	private Long productId;

	public GetProductInfosCollapser(Long productId) {
		this.productId = productId;
	}

	@Override
	public Long getRequestArgument() {
		return productId;
	}

	@Override
	protected HystrixCommand<List<ProductInfo>> createCommand(
			Collection<CollapsedRequest<ProductInfo, Long>> requests) {
		return new BatchGetProductInfosCommand(requests);
	}

	@Override
	protected void mapResponseToRequests(List<ProductInfo> batchResponse,
			Collection<CollapsedRequest<ProductInfo, Long>> requests) {
		int count = 0;
		for(CollapsedRequest<ProductInfo, Long> request : requests) {
			request.setResponse(batchResponse.get(count));
			count++;
		}
	}
	
	@Override
	protected String getCacheKey() {
		return "product_info_" + productId;
	}
 
	private static final class BatchGetProductInfosCommand extends HystrixCommand<List<ProductInfo>> {

		public final Collection<CollapsedRequest<ProductInfo, Long>> requests;

		public BatchGetProductInfosCommand(Collection<CollapsedRequest<ProductInfo, Long>> requests) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("batchGetProductInfosCollapserGroup"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("batchGetProductInfosCollapserCommand"))
					.andCommandPropertiesDefaults(
							HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500)));
			this.requests = requests;
		}

		@Override
		protected List<ProductInfo> run() throws Exception {
			//将多个商品拼接在一起,发送一次请求获取所有的结果
			StringBuilder builder = new StringBuilder("");
			for(CollapsedRequest<ProductInfo, Long> request : requests) {
				builder.append(String.valueOf(request.getArgument())).append(",");
			}
			String params = builder.substring(0, builder.length() - 1).toString();
			String url = "http://localhost:8081/product/getProductInfos?productIds=" + params;
			String response = HttpClientUtils.sendGetRequest(url);
			return JSONArray.parseArray(response, ProductInfo.class);
		}

	}

}
