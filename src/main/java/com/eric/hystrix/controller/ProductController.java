package com.eric.hystrix.controller;

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
}
