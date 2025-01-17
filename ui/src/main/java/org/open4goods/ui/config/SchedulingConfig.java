package org.open4goods.ui.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration

public class SchedulingConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(
			ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		// TODO : Virtual threads not working
//		return Executors.newScheduledThreadPool(5, Thread.ofVirtual().factory());
		return Executors.newScheduledThreadPool(5);
	}
}