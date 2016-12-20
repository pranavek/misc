package com.prnv.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import org.springframework.data.redis.core.RedisTemplate;

import com.prnv.load.PopulateDataUtilV1;
import com.prnv.model.ProductPrice;
import com.prnv.util.Constants;

/**
 * 
 * @author @pranavek
 *
 */
public class WriteTaskCallable implements Callable<String>{

	private RedisTemplate<String, ProductPrice> priceRedisTemplate;
	PopulateDataUtilV1 populateData = new PopulateDataUtilV1();
	ProductPrice price = (ProductPrice) populateData.generateData("com.prnv.model.ProductPrice");
	private String name;
	private int start;
	private int stop;
	
	int singleTotal = 0;
	

	public WriteTaskCallable(String name,RedisTemplate<String, ProductPrice> priceRedisTemplate, int start, int stop) {
		this.name = name;
		this.priceRedisTemplate = priceRedisTemplate;
		this.start = start;
		this.stop = stop;
	}

	@Override
	public String call() {
		
		long startTime = System.currentTimeMillis();
		insertToRedis();
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

	private void insertToRedis() {
		for (int i = start; i < stop; i++) {
			priceRedisTemplate.opsForHash().put("price",i, price);
		}
	}
	
	private void singleWrite(){
		List<Long> singleWrite = new ArrayList<>();
		IntStream.range(0, 1000).forEach(i ->{
			Long startTime = System.nanoTime();
			priceRedisTemplate.opsForHash().put("price",1, price);
			priceRedisTemplate.opsForHash().get("price", 1);
			Long endTime =  System.nanoTime();
			singleWrite.add((endTime - startTime));
		});
		
		singleWrite.forEach(i -> {
			singleTotal += i;
		});
	}
	
	

}
