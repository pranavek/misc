package com.prnv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.prnv.model.ProductPrice;
import com.prnv.tasks.ReadTaskCallable;
import com.prnv.util.Constants;

/**
 * 
 * @author @pranavek
 *
 */

public class RedisExecutorServiceMain {
	
	ApplicationContext context = new AnnotationConfigApplicationContext(SpringRedisTemplateApplication.class);
	@SuppressWarnings("unchecked")
	RedisTemplate<String, ProductPrice> priceRedisTemplate = (RedisTemplate<String, ProductPrice>) context
			.getBean("getProductPriceRedisTemplate");

	ExecutorService executorService = Executors.newCachedThreadPool();
	List<Callable<String>> callableTasks = new ArrayList<>();
	Map<String,Long> logMap = new HashMap<>();
	// iteration level
	int start = 0;
	int stop = 10000;
	int incrementBy = 10000;
	
	// Log level
	long readerTime = 0;
	long writerTime = 0;
	int noOfThreads = 1000;
	
	

	public static void main(String[] args) {
		new RedisExecutorServiceMain().init();
	}
	
	private void init(){
		IntStream.range(0,noOfThreads).forEach( i -> {
			callableTasks.add(new ReadTaskCallable(Constants.reader,priceRedisTemplate,start,stop));
			//callableTasks.add(new WriteTaskCallable(Constants.writer,priceRedisTemplate,start,stop));
			
			start = stop;
			stop += incrementBy;
			
		});
		
		List<Future<String>> futures;
		try {
			
			Map<String,Long> logMap = new HashMap<>();
			Long startTime = System.currentTimeMillis();
			futures = executorService.invokeAll(callableTasks);
			futures.forEach( future -> {
				try {
					String log = future.get();
					String[] logs = log.split(Constants.colonSep);
					logMap.put(logs[0],new Long(logs[1].trim()));					
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
			executorService.shutdown();
			Long endTime =  System.currentTimeMillis();
			logMap.forEach((k,v) -> {
				if(k.indexOf(Constants.writer) != -1){
					writerTime += v;
				}else{
					readerTime += v; 
				}
			});
			System.out.println("Time for execution is "+(endTime - startTime));
			System.out.println("Average time by "+Constants.writer+" is "+(writerTime/noOfThreads)+" & "+Constants.reader+" is "+(readerTime/noOfThreads));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

}
