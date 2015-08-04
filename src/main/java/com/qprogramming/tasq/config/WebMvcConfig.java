package com.qprogramming.tasq.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.security.core.Authentication;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

import com.qprogramming.tasq.account.Account;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

	private static final String MESSAGE_SOURCE = "/WEB-INF/i18n/messages";
	private static final String TILES = "/WEB-INF/tiles/tiles.xml";
	private static final String VIEWS = "/WEB-INF/views/**/views.xml";

	private static final String RESOURCES_HANDLER = "/resources/";
	private static final String RESOURCES_LOCATION = RESOURCES_HANDLER + "**";

	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping requestMappingHandlerMapping = super
				.requestMappingHandlerMapping();
		requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
		requestMappingHandlerMapping.setUseTrailingSlashMatch(false);
		return requestMappingHandlerMapping;
	}

	@Bean(name = "messageSource")
	public MessageSource configureMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGE_SOURCE);
		messageSource.setCacheSeconds(5);
		return messageSource;
	}

	@Bean
	public TilesViewResolver configureTilesViewResolver() {
		return new TilesViewResolver();
	}

	@Bean
	public TilesConfigurer configureTilesConfigurer() {
		TilesConfigurer configurer = new TilesConfigurer();
		configurer.setDefinitions(new String[] { TILES, VIEWS });
		return configurer;
	}

	@Bean(name = "sortResolver")
	public SortHandlerMethodArgumentResolver createSorter() {
		return new SortHandlerMethodArgumentResolver();
	}

	@Bean(name = "pageableResolver")
	public PageableHandlerMethodArgumentResolver createPagableHander() {
		return new PageableHandlerMethodArgumentResolver(createSorter());
	}
	
    @Bean
    public VelocityEngine getVelocityEngine() throws VelocityException, IOException{
        VelocityEngineFactory factory = new VelocityEngineFactory();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        factory.setVelocityProperties(props);
        return factory.createVelocityEngine();      
    }

	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(configureMessageSource());
		return validator;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(
				RESOURCES_LOCATION);
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	protected void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(createPagableHander());
		argumentResolvers.add(new UserDetailsHandlerMethodArgumentResolver());
	}

	// custom argument resolver inner classes

	private static class UserDetailsHandlerMethodArgumentResolver implements
			HandlerMethodArgumentResolver {

		public boolean supportsParameter(MethodParameter parameter) {
			return Account.class.isAssignableFrom(parameter.getParameterType());
		}

		public Object resolveArgument(MethodParameter parameter,
				ModelAndViewContainer modelAndViewContainer,
				NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
				throws Exception {
			Authentication auth = (Authentication) webRequest
					.getUserPrincipal();
			return auth != null && auth.getPrincipal() instanceof Account ? auth
					.getPrincipal() : null;
		}
	}
}
