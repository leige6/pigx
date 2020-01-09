package com.pig4cloud.pigx.common.data;


import com.pig4cloud.pigx.common.data.aspect.NoRepeatSubmitAspect;
import com.pig4cloud.pigx.common.data.utils.RedisUtils;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
public class DataAutoConfiguration {

	@Bean
	public RedisUtils redisUtils(){
		return  new RedisUtils();
	};

	@Bean
	public NoRepeatSubmitAspect noRepeatSubmitAspect() {
		return new NoRepeatSubmitAspect(redisUtils());
	}
}
