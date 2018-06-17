package com.eric.hystrix.controller;

import java.util.concurrent.Future;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eric.hystrix.command.GetCityNameCommand;
import com.eric.hystrix.command.GetProductInfoCommand;
import com.eric.hystrix.command.GetProductInfosCommand;
import com.eric.hystrix.model.ProductInfo;
import com.eric.hystrix.utils.HttpClientUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixObservableCommand;

import rx.Observable;
import rx.Observer;

/**
 * 缓存服务控制类
 * @author pxl
 *
 */
@Controller
@RequestMapping("cache")
public class CacheController {
	
	/**
	 * 没有做资源隔离的缓存服务调用商品服务接口
	 * 若商品服务响应过慢会导致调用线程短时间无法释放,必须等超时
	 * 并发量大的情况会耗尽所有线程导致其他服务部可用
	 * @param productId
	 * @return
	 */
	@RequestMapping("getProductInfoWithoutHystrix")
	@ResponseBody
	public String getProductInfoWithoutHystrix(Long productId) {
		String url = "http://localhost:8081/product/getProductInfo?productId=" + productId;
		String productJson = HttpClientUtils.sendGetRequest(url);
		System.out.println(productJson);
		return "success";
	}
	
	/**
	 * 使用hystrix做资源隔离(同步)请求商品服务的http接口获得单个商品信息
	 * @param productId
	 * @return
	 */
	@RequestMapping("getProductInfoWithHystrixSync")
	@ResponseBody
	public String getProductInfoWithHystrixSync(Long productId) {
		HystrixCommand<ProductInfo> command1 = new GetProductInfoCommand(productId);
		//同步调用command中的run方法
		ProductInfo productInfo;
		try {
			productInfo = command1.execute();
			Long cityId = productInfo.getCityId();
			HystrixCommand<String> command2 = new GetCityNameCommand(cityId);
			String cityName = command2.execute();
			productInfo.setCityName(cityName);
			System.out.println(productInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		return "success";
	}
	
	/**
	 * 使用hystrix做资源隔离(异步)请求商品服务的http接口获得单个商品信息
	 * @param productId
	 * @return
	 */
	@RequestMapping("getProductInfoWithHystrixAsync")
	@ResponseBody
	public String getProductInfoWithHystrixAsync(Long productId) {
		HystrixCommand<ProductInfo> command = new GetProductInfoCommand(productId);
		//异步调用command中的run方法
		Future<ProductInfo> queue = command.queue();
		try {
			//此处可以执行其他业务逻辑
			Thread.sleep(1000);
			//异步线程执行完成后获取返回值
			while(queue.isDone()) {
				ProductInfo productInfo = queue.get();
				System.out.println(productInfo);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		return "success";
	}
	
	/**
	 * 使用hystrix做资源隔离请求商品服务的http接口获得多个商品信息
	 * @param productId
	 * @return
	 */
	@RequestMapping("getProductInfosWithHystrix")
	@ResponseBody
	public String getProductInfosWithHystrix(String productIds) {
		HystrixObservableCommand<ProductInfo> command = new GetProductInfosCommand(productIds.split(","));
		Observable<ProductInfo> observable = command.observe();
		// 等到调用subscribe然后才会执行
		observable.subscribe(new Observer<ProductInfo>() { 
			
			public void onCompleted() {
				System.out.println("获取完了所有的商品数据");
			}
			
			public void onError(Throwable e) {
				e.printStackTrace();
			}
			
			public void onNext(ProductInfo productInfo) {
				System.out.println(productInfo);  
			}
			
		});
		return "success";
	}
	
	/**
	 * 对于一次请求的重复数据走hystrix请求缓存
	 * 不再重复执行相关查询方法
	 * 如productids=1,1,2,3,1,3
	 * @param productIds
	 * @return
	 */
	@RequestMapping("getProductInfosWithRequestCache")
	@ResponseBody
	public String getProductInfosWithRequestCache(String productIds) {
		try {
			for(String productId : productIds.split(",")) {
				GetProductInfoCommand command = new GetProductInfoCommand(Long.valueOf(productId));
				ProductInfo product = command.execute();
				System.out.println(product);
				System.out.println("是否从hystrix请求上下文缓存中获取数据:" + command.isResponseFromCache());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "fail:" + e.getMessage();
		}
		return "success";
	}
}
