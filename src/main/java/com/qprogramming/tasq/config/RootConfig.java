package com.qprogramming.tasq.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@ComponentScan(basePackages = {"com.qprogramming.tasq"})
public class RootConfig {

    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        String propertyLocation = System.getProperty("properties.location");
        if (StringUtils.isNotBlank(propertyLocation)) {
            ppc.setLocations(new FileSystemResource(propertyLocation));
        } else {
            ppc.setLocations(new ClassPathResource("/application.properties"));
        }
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getMultipartResolver() {
        return new CommonsMultipartResolver();
    }

}