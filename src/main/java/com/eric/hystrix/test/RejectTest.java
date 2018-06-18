package com.eric.hystrix.test;

import java.util.concurrent.CountDownLatch;

import com.eric.hystrix.utils.HttpClientUtils;

/**
 * 测试接口限流
 * @author pxl
 *
 */
public class RejectTest {
	
	private static int threadNum = 25;
	
	private static CountDownLatch semaphore = new CountDownLatch(threadNum);
	
	public static void main(String[] args) {
		//command中设置的线程池大小为10,最大队列拒绝数为8,故并发调用成功18次,失败7次
		for(int i = 0; i < threadNum; i ++) {
			new TestThread(i).start();
			semaphore.countDown();
		}
	}
	
	private static class TestThread extends Thread {
		
		private int index;
		
		public TestThread(int index) {
			this.index = index;
		}
		
		@Override
		public void run() {
			try {
				semaphore.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String url = "http://localhost:8081/cache/getProductInfoTestReject?productId=1";
			System.out.println("第" + index + "次执行");
			HttpClientUtils.sendGetRequest(url);
		}
	}

}
