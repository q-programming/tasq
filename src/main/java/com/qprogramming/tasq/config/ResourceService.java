package com.qprogramming.tasq.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class ResourceService implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Resource getResource(String location) {
		return resourceLoader.getResource(location);
	}

	/**
	 * Builds map with basic resources like logo
	 * 
	 * @return
	 */
	public Map<String, Resource> getBasicResourceMap() {
		Map<String, Resource> map = new HashMap<String, Resource>();
		map.put("logo", getLogo());
		return map;
	}

	public Resource getLogo() {
		return getResource("classpath:email/img/tasQ_logo_small.png");
	}

	public Resource getTaskTypeIcon(String type) {
		return getResource("classpath:email/img/" + type + ".png");
	}

}
