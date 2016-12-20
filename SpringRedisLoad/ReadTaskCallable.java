package com.prnv.tasks;

import java.util.concurrent.Callable;

import org.springframework.data.redis.core.RedisTemplate;


import com.prnv.model.ProductPrice;
import com.prnv.util.Constants;

/**
 * 
 * @author @pranavek
 *
 */
public class ReadTaskCallable implements Callable<String> {
	
	RedisTemplate<String, ProductPrice> priceRedisTemplate;
	private String name;
	private int start;
	private int stop;

	public ReadTaskCallable(String name,RedisTemplate<String, ProductPrice> priceRedisTemplate, int start, int stop) {
		this.name = name;
		this.priceRedisTemplate = priceRedisTemplate;
		this.start = start;
		this.stop = stop;
	}

	@Override
	public String call() throws Exception {
		long startTime = System.currentTimeMillis();
		readFromRedis();
		long endTime = System.currentTimeMillis();
		
		StringBuffer b = new StringBuffer();
		b.append(name);
		b.append(" ");
		b.append(Constants.hypenSep);
		b.append(" ");
		b.append(Thread.currentThread().getName());
		b.append(" ");
		b.append(Constants.colonSep);
		b.append(" ");
		b.append((endTime - startTime));
		
		return b.toString();
	}
	
	private void readFromRedis() {
		for (int i = start; i < stop; i++) {
			priceRedisTemplate.opsForHash().get("price", i);
			// System.out.println(price.getBogoDiscount());
		}
	}
	
	

}
