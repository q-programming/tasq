package com.qprogramming.tasq.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.support.Utils;

@Service
public class ResourceService implements ResourceLoaderAware {

	@Value("${home.directory}")
	private String tasqRootDir;
	private static final String AVATAR_DIR = "avatar";
	private static final String SMALL_LOGO = "small_logo";
	private static final String PNG = ".png";
	private ResourceLoader resourceLoader;

	private static final Logger LOG = LoggerFactory.getLogger(ResourceService.class);

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
		try {
			map.put("logo", getLogo());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return map;
	}

	public Resource getLogo() throws IOException {
		return getResource("file:" + getAvatarDir() + SMALL_LOGO + PNG);
	}

	public Resource getUserAvatar() {
		return getResource("file:" + getAvatarDir() + Utils.getCurrentAccount().getId() + PNG);
	}

	public Resource getTaskTypeIcon(String type) {
		return getResource("classpath:email/img/" + type + PNG);
	}

	private String getAvatarDir() {
		return tasqRootDir + File.separator + AVATAR_DIR + File.separator;
	}

}
