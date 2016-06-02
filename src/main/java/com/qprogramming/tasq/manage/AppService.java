package com.qprogramming.tasq.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class AppService {

    public static final String EMAIL_HOST = "emailHost";
    public static final String EMAIL_PORT = "emailPort";
    public static final String EMAIL_USERNAME = "emailUsername";
    public static final String EMAIL_PASS = "emailPass";
    public static final String EMAIL_SMTPAUTH = "emailSmtpAuth";
    public static final String EMAIL_SMTPSTARTTLS = "emailSmtpStarttls";
    public static final String EMAIL_ENCODING = "emailEncoding";
    public static final String EMAIL_DOMAIN = "emailDomain";
    public static final String DEFAULTLANG = "defaultLang";
    public static final String TASQROOTDIR = "tasqRootDir";
    public static final String URL = "url";
    public static final String DEFAULTROLE = "defaultRole";

    private static final Logger LOG = LoggerFactory.getLogger(AppService.class);

    private String url;

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private String emailPort;

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.pass}")
    private String emailPass;

    @Value("${email.smtp.auth}")
    private String emailSmtpAuth;

    @Value("${email.smtp.starttls}")
    private String emailSmtpStarttls;

    @Value("${email.encoding}")
    private String emailEncoding;

    @Value("${email.domain}")
    private String emailDomain;

    @Value("${default.locale}")
    private String defaultLang;

    @Value("${default.role}")
    private String defaultRole;


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
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                LOG.error("Error while getting property ", e);
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
