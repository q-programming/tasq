package com.qprogramming.tasq.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@ComponentScan(basePackages = {"com.qprogramming.tasq"})
public class RootConfig {

    /**
     * Try to load properties file. First VM arg is searched, then Context paramater , and if non was passed default
     * application.properties is taken from resources
     *
     * @param environment
     * @return
     */
    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer(Environment environment) {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        String propertyLocation = System.getProperty("properties.location");
        String contextPropertyLocation = environment.getProperty("propertiesPath");
        if (StringUtils.isNotBlank(propertyLocation)) {
            ppc.setLocations(new FileSystemResource(propertyLocation));
        } else if (StringUtils.isNotBlank(contextPropertyLocation)) {
            ppc.setLocations(new FileSystemResource(contextPropertyLocation));
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