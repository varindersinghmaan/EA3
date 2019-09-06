package org.pstcl.ea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class SpringAsyncConfig { 

	@Bean(value = "eaThreadPoolTaskExecutor")
	public ThreadPoolTaskExecutor  threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor poolTaskExecutor= new ThreadPoolTaskExecutor();
		poolTaskExecutor.setCorePoolSize(30);
		poolTaskExecutor.setMaxPoolSize(70);
		return poolTaskExecutor;
	}
	

}