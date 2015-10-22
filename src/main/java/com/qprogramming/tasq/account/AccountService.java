/**
 * 
 */
package com.qprogramming.tasq.account;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.fasterxml.uuid.Generators;
import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.support.Utils;

/**
 * @author romanjak
 * @date 21 maj 2014
 */
@Service
public class AccountService {

	private static final String APPLICATION = "application";

	private static final String LINK = "link";

	private static final String ACCOUNT = "account";

	private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

	@Value("${default.locale}")
	private String defaultLang;

	private AccountRepository accRepo;
	private MessageSource msg;
	private VelocityEngine velocityEngine;
	private ResourceService resourceSrv;
	private MailMail mailer;
	private PasswordEncoder passwordEncoder;
	private AppService appSrv;

	// @PersistenceContext
	// private EntityManager entityManager;

	@Autowired
	public AccountService(AccountRepository accRepo, MessageSource msg, VelocityEngine velocityEngine,
			ResourceService resourceSrv, MailMail mailer, PasswordEncoder passwordEncoder, AppService appSrv) {
		this.accRepo = accRepo;
		this.msg = msg;
		this.velocityEngine = velocityEngine;
		this.resourceSrv = resourceSrv;
		this.mailer = mailer;
		this.passwordEncoder = passwordEncoder;
		this.appSrv = appSrv;
	}

	@Transactional
	public Account save(Account account, boolean passwordReset) {
		if (passwordReset) {
			account.setPassword(passwordEncoder.encode(account.getPassword()));
		}
		if (account.getLanguage() == null) {
			account.setLanguage(defaultLang);
		}
		UUID uuid = Generators.timeBasedGenerator().generate();
		account.setUuid(uuid.toString());
		accRepo.save(account);
		// entityManager.persist(account);
		return account;
	}

	public Account findByEmail(String email) {
		return accRepo.findByEmail(email);
	}

	public Account findByUsername(String username) {
		return accRepo.findByUsername(username);
	}

	public Account findByUuid(String uiid) {
		return accRepo.findByUuid(uiid);
	}

	/**
	 * @param account
	 */
	@Transactional
	public Account update(Account account) {
		return accRepo.save(account);
	}

	public Account findById(Long id) {
		return accRepo.findById(id);
	}

	/**
	 * @return
	 */
	public List<Account> findAll() {
		return accRepo.findAll();
	}

	public Page<Account> findAll(Pageable p) {
		return accRepo.findAll(p);
	}

	public Page<Account> findByNameSurnameContaining(String term, Pageable p) {
		return accRepo.findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCase(term, term, p);
	}

	public List<Account> findByNameSurnameContaining(String term) {
		return accRepo.findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCase(term, term);
	}

	public List<Account> findAdmins() {
		return accRepo.findByRole(Roles.ROLE_ADMIN);
	}

	public void sendConfirmationLink(Account account) {
		String baseUrl = appSrv.getProperty(AppService.URL);
		String confirmlink = baseUrl + "/confirm?id=" + account.getUuid();
		String subject = msg.getMessage("signup.register", null, Utils.getDefaultLocale());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(ACCOUNT, account);
		model.put(LINK, confirmlink);
		model.put(APPLICATION, appSrv.getProperty(AppService.URL));
		String message = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				"email/" + Utils.getDefaultLocale() + "/register.vm", "UTF-8", model);
		mailer.sendMail(MailMail.REGISTER, account.getEmail(), subject, message, resourceSrv.getBasicResourceMap());
	}

	public void sendResetLink(Account account) {
		String baseUrl = appSrv.getProperty(AppService.URL);
		StringBuilder url = new StringBuilder(baseUrl);
		url.append("/");
		url.append("password?id=");
		url.append(account.getUuid());
		String subject = msg.getMessage("singin.password.reset", null, new Locale(account.getLanguage()));
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(ACCOUNT, account);
		model.put(LINK, url);
		model.put(APPLICATION, baseUrl);
		String message = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				"email/" + account.getLanguage() + "/password.vm", "UTF-8", model);
		LOG.info(url.toString());
		mailer.sendMail(MailMail.OTHER, account.getEmail(), subject, message, resourceSrv.getBasicResourceMap());
	}

}
