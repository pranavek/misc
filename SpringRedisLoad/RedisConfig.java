package com.prnv.config;

import java.net.UnknownHostException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.prnv.model.Person;
import com.prnv.model.ProductPrice;

@Configuration
public class RedisConfig {

  @Bean
  @ConditionalOnMissingBean
  public RedisConnectionFactory redisConnectionFactory() 
                                      throws UnknownHostException { 
      JedisConnectionFactory factory = 
                                  new JedisConnectionFactory(); 
      factory.setHostName("localhost"); 
      factory.setPort(6379); 
      return factory; 
  } 
   
  @Bean
  @ConditionalOnMissingBean(name = "redisTemplate") 
  public RedisOperations<Object, Object> redisTemplate(
                          RedisConnectionFactory redisConnectionFactory) 
                                   throws UnknownHostException { 
      RedisTemplate<Object, Object> template = new RedisTemplate<>(); 
      template.setConnectionFactory(redisConnectionFactory); 
      return template; 
  } 
   
  @Bean
  @ConditionalOnMissingBean(StringRedisTemplate.class) 
  public StringRedisTemplate stringRedisTemplate(
                          RedisConnectionFactory redisConnectionFactory) 
                                throws UnknownHostException { 
      StringRedisTemplate template = new StringRedisTemplate(); 
      template.setConnectionFactory(redisConnectionFactory); 
      return template; 
  } 
  
  @Bean
  public RedisTemplate<String, Person> getPersonRedisTemplate(
                   RedisConnectionFactory redisConnectionFactory) { 
      RedisTemplate<String, Person> t = new RedisTemplate<>(); 
      t.setConnectionFactory(redisConnectionFactory); 
      t.setKeySerializer(new StringRedisSerializer()); 
      t.setValueSerializer(new Jackson2JsonRedisSerializer<>(Person.class)); 
      t.afterPropertiesSet(); 
      return t; 
  } 
  
  @Bean
  public RedisTemplate<String, ProductPrice> getProductPriceRedisTemplate(
                   RedisConnectionFactory redisConnectionFactory) { 
      RedisTemplate<String, ProductPrice> t = new RedisTemplate<>(); 
      t.setConnectionFactory(redisConnectionFactory); 
      t.setKeySerializer(new StringRedisSerializer()); 
      t.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProductPrice.class)); 
      t.afterPropertiesSet(); 
      return t; 
  } 
  
}
