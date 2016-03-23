package com.qprogramming.tasq.mail;

import com.qprogramming.tasq.manage.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Service
public class MailMail {

    public static final int NOTIFICATION = 0;
    public static final int MESSAGE = 1;
    public static final int PROJECT = 2;
    public static final int REGISTER = 3;
    public static final int OTHER = -1;

    private static final String TASQ = "tasq@";
    private static final String TASQ_PERSONAL = "Tasker";
    private static final String TASQ_REGISTER = "registration@";
    private static final String TASQ__REGISTER_PERSONAL = "Registration at Tasker";
    private static final String NOTIFICATION_TASQ = "notification@";
    private static final String NOTIFICATION_TASQ_PERSONAL = "Tasker notifier";
    private static final String MESSAGE_TASQ = "messages@";
    private static final String MESSAGE_TASQ_PERSONAL = "Tasker messenger";
    private static final String PROJECT_TASQ = "projects@";
    private static final String PROJECT_TASQ_PERSONAL = "Tasker projects";
    private static final Logger LOG = LoggerFactory.getLogger(MailMail.class);
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

    private JavaMailSenderImpl jmsi;

    private MailSender mailSender;
    private AppService appSrv;

    @Autowired
    public MailMail(AppService appSrv) {
        this.appSrv = appSrv;
        initMailSender();
    }

    //	@Bean
//	public MailMail mailMail() {
//		MailMail mailMail = new MailMail();
//
//		return mailMail;
//	}
//


//	@Bean
//	@Autowired
//	public MailSender mailSender(AppService appSrv) {
//		this.appSrv = appSrv;
//		initMailSender();
//		return mailSender;
//	}

    /**
     * Initialize mail sender with properties form DB/properties file
     */
    public void initMailSender() {
        jmsi = new JavaMailSenderImpl();
        jmsi.setHost(appSrv.getProperty(AppService.EMAIL_HOST));
        jmsi.setPort(Integer.parseInt(appSrv.getProperty(AppService.EMAIL_PORT)));
        jmsi.setUsername(appSrv.getProperty(AppService.EMAIL_USERNAME));
        jmsi.setPassword(appSrv.getProperty(AppService.EMAIL_PASS));
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", appSrv.getProperty(AppService.EMAIL_SMTPAUTH));
        javaMailProperties.setProperty("mail.smtp.starttls.enable", appSrv.getProperty(AppService.EMAIL_SMTPSTARTTLS));
        jmsi.setJavaMailProperties(javaMailProperties);
        mailSender = jmsi;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send email
     *
     * @param type
     * @param to
     * @param subject
     * @param msg
     * @return true if there were no errors while sending
     */
    public boolean sendMail(int type, String to, String subject, String msg, Map<String, Resource> resources) {
        try {
            MimeMessage message = ((JavaMailSenderImpl) mailSender).createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,
                    appSrv.getProperty(AppService.EMAIL_ENCODING));
            switch (type) {
                case NOTIFICATION:
                    helper.setFrom(domain(NOTIFICATION_TASQ), NOTIFICATION_TASQ_PERSONAL);
                    break;
                case MESSAGE:
                    helper.setFrom(domain(MESSAGE_TASQ), MESSAGE_TASQ_PERSONAL);
                    break;
                case PROJECT:
                    helper.setFrom(domain(PROJECT_TASQ), PROJECT_TASQ_PERSONAL);
                    break;
                case REGISTER:
                    helper.setFrom(domain(TASQ_REGISTER), TASQ__REGISTER_PERSONAL);
                    break;
                default:
                    helper.setFrom(domain(TASQ), TASQ_PERSONAL);
                    break;
            }
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(msg, true);
            // Load logos and other stuff
            for (Map.Entry<String, Resource> entry : resources.entrySet()) {
                helper.addInline(entry.getKey(), entry.getValue());
            }
            LOG.debug("Sending e-mail to:" + to);
            ((JavaMailSenderImpl) mailSender).send(message);
        } catch (MailSendException e) {
            LOG.error(e.getLocalizedMessage());
            return false;
        } catch (MessagingException e) {
            LOG.error(e.getLocalizedMessage());
            return false;
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public Locale getDefaultLang() {
        return new Locale(defaultLang);
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    private String domain(String var) {
        return var + appSrv.getProperty(AppService.EMAIL_DOMAIN);
    }
}