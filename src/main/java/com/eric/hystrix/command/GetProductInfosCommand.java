package com.eric.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.eric.hystrix.model.ProductInfo;
import com.eric.hystrix.utils.HttpClientUtils;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GetProductInfosCommand extends HystrixObservableCommand<ProductInfo>{
	
	private String[] productIds;
	
	public GetProductInfosCommand(String[] productIds) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductInfosGroup")));
		this.productIds = productIds;
	}
	
	@Override
	protected Observable<ProductInfo> construct() {
		return Observable.create(new Observable.OnSubscribe<ProductInfo>() {

			@Override
			public void call(Subscriber<? super ProductInfo> observer) {
				try {
					for(String productId : productIds) {
						String url = "http://localhost:8081/product/getProductInfo?productId=" + productId;
						String productJson = HttpClientUtils.sendGetRequest(url);
						ProductInfo productInfo = JSONObject.parseObject(productJson, ProductInfo.class); 
						observer.onNext(productInfo); 
					}
					observer.onCompleted();
				} catch (Exception e) {
					observer.onError(e);
				}
			}
		}).subscribeOn(Schedulers.io());
	}

}
