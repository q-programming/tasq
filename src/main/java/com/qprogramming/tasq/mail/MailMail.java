package com.qprogramming.tasq.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.support.Utils;

@Configuration
public class MailMail {

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

	private static final Logger LOG = LoggerFactory.getLogger(MailMail.class);

	private JavaMailSenderImpl jmsi;

	public static final int NOTIFICATION = 0;
	public static final int MESSAGE = 1;
	public static final int PROJECT = 2;
	public static final int REGISTER = 3;
	public static final int OTHER = -1;

	private static final String TASQ = "tasq@tasq.com";
	private static final String TASQ_PERSONAL = "TasQ";
	private static final String TASQ_REGISTER = "registration@tasq.com";
	private static final String TASQ__REGISTER_PERSONAL = "Registration at TasQ";
	private static final String NOTIFICATION_TASQ = "notification@tasq.com";
	private static final String NOTIFICATION_TASQ_PERSONAL = "TasQ notifier";
	private static final String MESSAGE_TASQ = "messages@tasq.com";
	private static final String MESSAGE_TASQ_PERSONAL = "TasQ messenger";
	private static final String PROJECT_TASQ = "projects@tasq.com";
	private static final String PROJECT_TASQ_PERSONAL = "TasQ projects";

	@Autowired
	private MailSender mailSender;

	@Autowired
	private ResourceService resourceSrv;

	@Bean
	public MailMail mailMail() {
		MailMail mailMail = new MailMail();

		return mailMail;
	}

	@Bean
	public MailSender mailSender() {
		jmsi = new JavaMailSenderImpl();
		jmsi.setHost(host);

		jmsi.setPort(Integer.parseInt(port));
		jmsi.setUsername(username);
		jmsi.setPassword(pass);

		Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.smtp.auth", smtpAuth);
		javaMailProperties.setProperty("mail.smtp.starttls.enable", smtpStarttls);
		jmsi.setJavaMailProperties(javaMailProperties);
		mailSender = jmsi;
		return mailSender;

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
	public boolean sendMail(int type, String to, String subject, String msg) {
		try {
			MimeMessage message = ((JavaMailSenderImpl) mailSender).createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, encoding);
			switch (type) {
			case NOTIFICATION:
				helper.setFrom(NOTIFICATION_TASQ, NOTIFICATION_TASQ_PERSONAL);
				break;
			case MESSAGE:
				helper.setFrom(MESSAGE_TASQ, MESSAGE_TASQ_PERSONAL);
				break;
			case PROJECT:
				helper.setFrom(PROJECT_TASQ, PROJECT_TASQ_PERSONAL);
				break;
			case REGISTER:
				helper.setFrom(TASQ_REGISTER, TASQ__REGISTER_PERSONAL);
				break;
			default:
				helper.setFrom(TASQ, TASQ_PERSONAL);
				break;
			}
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(msg, true);
			// Load logos and other stuff
			Resource logoRes = resourceSrv.getResource("classpath:email/img/tasQ_logo_small.png");
			helper.addInline("logo", logoRes);
			LOG.debug("Sending e-mail to:" + to);
			((JavaMailSenderImpl) mailSender).send(message);
		} catch (MailSendException e) {
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
}