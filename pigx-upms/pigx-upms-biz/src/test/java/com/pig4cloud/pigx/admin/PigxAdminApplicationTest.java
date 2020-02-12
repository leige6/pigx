/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.pig4cloud.pigx.admin;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lengleng
 * @date 2018/10/7
 * <p>
 * 加解密单元测试
 */
public class PigxAdminApplicationTest {

	static Map<Integer,Integer>  map=new HashMap<>(2);

	@Test
	public void testJasypt() {
		// 对应application-dev.yml 中配置的根密码
		System.setProperty("jasypt.encryptor.password", "pigx");
		StringEncryptor stringEncryptor = new DefaultLazyEncryptor(new StandardEnvironment());

		//加密方法
		System.out.println(stringEncryptor.encrypt("pigx"));

		//解密方法
		System.out.println(stringEncryptor.decrypt("ltJPpR50wT0oIY9kfOe1Iw==="));
	}


	@Test
	public void testHashMap() throws InterruptedException{
		final AtomicInteger at=new AtomicInteger(0);
		final CountDownLatch countDownLatch=new CountDownLatch(100);//并发计数器
		for (int i=0;i<100;i++){
           new Thread(new Runnable() {
			   @Override
			   public void run() {
				  while (at.get()<1000000){
                     //向map中添加元素
					  map.put(at.get(),at.get());
					  at.incrementAndGet();//对int加1
				  }
				   countDownLatch.countDown();
			   }
		   }
		   ).start();
		}
        //阻塞当前主线程
		countDownLatch.await();
		Set<Integer> keySet=map.keySet();
		Iterator<Integer> iterator=keySet.iterator();
		while (iterator.hasNext()){
			Integer key= iterator.next();
			System.out.println("key:"+key+"- -value:"+map.get(key));
		}
	}
}
