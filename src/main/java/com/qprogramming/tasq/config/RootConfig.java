package com.qprogramming.tasq.config;

import org.springframework.context.annotation.*;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan(basePackages = { "com.qprogramming.tasq" })
public class RootConfig {

	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		ppc.setLocations(new Resource[] {
				new ClassPathResource("/persistence.properties"),
				new ClassPathResource("/project.properties"),
				new ClassPathResource("/email.properties"), });
		return ppc;
	}

}