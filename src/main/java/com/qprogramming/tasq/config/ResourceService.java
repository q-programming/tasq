package com.qprogramming.tasq.config;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.support.Utils;

@Service
public class ResourceService implements ResourceLoaderAware {

	private static final String AVATAR_DIR = "avatar";
	private static final String SMALL = "small_";
	private static final String LOGO = "logo";
	private static final String PNG = ".png";
	private ResourceLoader resourceLoader;
	private AppService appSrv;

	@Autowired
	public ResourceService(AppService appSrv) {
		this.appSrv = appSrv;
	}

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
		return getResource("file:" + getAvatarDir() + SMALL + LOGO + PNG);
	}

	public Resource getUserAvatar() {
		try {
			resizeAvatar();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return getResource("file:" + getAvatarDir() + SMALL + Utils.getCurrentAccount().getId() + PNG);
	}

	public Resource getTaskTypeIcon(String type) {
		return getResource("classpath:email/img/" + type + PNG);
	}

	private String getAvatarDir() {
		return appSrv.getProperty(AppService.TASQROOTDIR) + File.separator + AVATAR_DIR + File.separator;
	}

	private void resizeAvatar() throws IOException {
		String avatar = getAvatarDir() + Utils.getCurrentAccount().getId() + PNG;
		String resizedavatar = getAvatarDir() + SMALL + Utils.getCurrentAccount().getId() + PNG;
		BufferedImage originalImage = ImageIO.read(new File(avatar));
		BufferedImage scaledImg = Scalr.resize(originalImage, 50);
		ImageIO.write(scaledImg, "png", new File(resizedavatar));
	}

	public void clean() {
		String resizedavatar = getAvatarDir() + SMALL + Utils.getCurrentAccount().getId() + PNG;
		File file = new File(resizedavatar);
		if (file.exists()) {
			file.delete();
		}
	}

}
