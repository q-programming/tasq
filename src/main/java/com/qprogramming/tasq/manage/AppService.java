package com.qprogramming.tasq.manage;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppService {

	public static final String HOST = "host";
	public static final String URL = "url";

	private static final Logger LOG = LoggerFactory.getLogger(AppService.class);

	private String url;

	@Value("${email.host}")
	private String host;

	@Value("${email.port}")
	private String port;

	@Value("${email.username}")
	private String username;

	@Value("${email.pass}")
	private String pass;

	@Value("${email.smtp.auth}")
	private String smtpAuth;

	@Value("${email.smtp.starttls}")
	private String smtpStarttls;

	@Value("${email.encoding}")
	private String encoding;

	@Value("${default.locale}")
	private String defaultLang;

	@Value("${home.directory}")
	private String tasqRootDir;

	private PropertyRepository propRepo;

	@Autowired
	public AppService(PropertyRepository propRepo) {
		this.propRepo = propRepo;
	}

	public String getProperty(String key) {
		Property prop = propRepo.findByKey(key);
		if (prop == null) {
			try {
				Field f = AppService.class.getDeclaredField(key);
				f.setAccessible(true);
				return (String) f.get(this);
			} catch (IllegalArgumentException e) {
				LOG.error(e.getMessage());
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage());
			} catch (NoSuchFieldException e) {
				LOG.error(e.getMessage());
			} catch (SecurityException e) {
				LOG.error(e.getMessage());
			}
		}
		return prop.getValue();
	}

	public void setProperty(String key, String value) {
		Property prop = propRepo.findByKey(key);
		if (prop == null) {
			prop = new Property(key);
		}
		prop.setValue(value);
		propRepo.save(prop);
	}
}
