package com.eric.hystrix.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eric.hystrix.model.ProductInfo;

/**
 * 商品服务控制类
 * @author pxl
 *
 */
@Controller
@RequestMapping("/product")
public class ProductController {
	
	/**
	 * 模拟调用商品服务接口获取商品详情信息
	 * @param productId
	 * @return
	 */
	@RequestMapping("/getProductInfo")
	@ResponseBody
	public ProductInfo getProductInfo(Long productId) {
		System.out.println("接收到一次getProductInfo请求,productId = " + productId);
		ProductInfo product = new ProductInfo();
		product.setId(productId);
		product.setName("iPhoneX 64G");
		product.setColor("中国红");
		product.setPrice(7488D);
		product.setSize("150*50*8");
		product.setModifiedTime("2018-06-06 10:00:00");
		product.setShopId(1L);
		product.setCityId(1L);
		product.setBrandId(1L);
		return product;
	}
	
	
	/**
	 * 模拟调用商品服务接口获取商品详情信息
	 * @param productId
	 * @return
	 */
	@RequestMapping("/getProductInfos")
	@ResponseBody
	public List<ProductInfo> getProductInfos(String productIds) {
		System.out.println("接收到一次getProductInfos请求,productIds = " + productIds);
		List<ProductInfo> list = new ArrayList<ProductInfo>();
		for(String productId : productIds.split(",")) {
			ProductInfo product = new ProductInfo();
			product.setId(Long.valueOf(productId));
			product.setName("iPhoneX 64G");
			product.setColor("中国红");
			product.setPrice(7488D);
			product.setSize("150*50*8");
			product.setModifiedTime("2018-06-06 10:00:00");
			product.setShopId(1L);
			product.setCityId(1L);
			product.setBrandId(1L);
			list.add(product);
		}
		return list;
	}
}
