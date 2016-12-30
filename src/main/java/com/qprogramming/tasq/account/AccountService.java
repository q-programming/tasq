/**
 *
 */
package com.qprogramming.tasq.account;

import com.fasterxml.uuid.Generators;
import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.manage.Theme;
import com.qprogramming.tasq.support.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @author romanjak
 * @date 21 maj 2014
 */
@Service
public class AccountService {

    private static final String CUR_ACCOUNT = "curAccount";
    private static final String THEME = "theme";
    private static final String AVATAR = "avatar";
    private static final String APPLICATION = "application";
    private static final String LINK = "link";
    private static final String APPLICATION_NAME = "applicationName";
    private static final String ACCOUNT = "account";
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
    private static final String UTF_8 = "UTF-8";
    private static final String EMAIL_TEMP_PATH = "email/";
    @Value("${default.locale}")
    private String defaultLang;

    private AccountRepository accRepo;
    private MessageSource msg;
    private VelocityEngine velocityEngine;
    private ResourceService resourceSrv;
    private MailMail mailer;
    private PasswordEncoder passwordEncoder;
    private AppService appSrv;
    private String applicationName;

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
        applicationName = appSrv.getProperty(AppService.APPLICATION_NAME);
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
        return accRepo.save(account);
        // entityManager.persist(account);
    }

    public Account findByEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            return accRepo.findByEmail(email.toLowerCase());
        }
        return null;
    }

    public Account findByUsername(String username) {
        if (StringUtils.isNotBlank(username)) {
            return accRepo.findByUsername(username.toLowerCase());
        }
        return null;
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

    /**
     * Bulk updates all accounts from list
     *
     * @param accounts accounts list
     */
    @Transactional
    public void update(List<Account> accounts) {
        accRepo.save(accounts);
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

    public List<Account> findAllWithActiveTask(String taskID) {
        return accRepo.findByActiveTask(taskID);
    }

    public Page<Account> findAll(Pageable p) {
        return accRepo.findAll(p);
    }

    public Page<Account> findByNameSurnameContaining(String term, Pageable p) {
        return accRepo.findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(term, term, term, p);
    }

    public List<Account> findByNameSurnameContaining(String term) {
        return accRepo.findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(term, term, term);
    }

    public List<Account> findAdmins() {
        return accRepo.findByRole(Roles.ROLE_ADMIN);
    }

    public boolean sendConfirmationLink(Account account) {
        String baseUrl = appSrv.getProperty(AppService.URL);
        String confirmlink = baseUrl + "/confirm?id=" + account.getUuid();
        String subject = msg.getMessage("signup.register", new Object[]{applicationName}, Utils.getDefaultLocale());
        StringWriter stringWriter = new StringWriter();
        VelocityContext context = new VelocityContext();
        context.put(ACCOUNT, account);
        context.put(LINK, confirmlink);
        context.put(APPLICATION, baseUrl);
        context.put(APPLICATION_NAME, applicationName);
        velocityEngine.mergeTemplate(EMAIL_TEMP_PATH + Utils.getDefaultLocale() + "/register.vm", UTF_8, context, stringWriter);
        String message = stringWriter.toString();
        return mailer.sendMail(MailMail.REGISTER, account.getEmail(), subject, message,
                resourceSrv.getBasicResourceMap());
    }

    public boolean sendResetLink(Account account) {

        String baseUrl = appSrv.getProperty(AppService.URL);
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("/");
        url.append("password?id=");
        url.append(account.getUuid());
        String subject = msg.getMessage("singin.password.reset", new Object[]{applicationName}, new Locale(account.getLanguage()));
        VelocityContext context = new VelocityContext();
        StringWriter stringWriter = new StringWriter();
        context.put(ACCOUNT, account);
        context.put(LINK, url);
        context.put(APPLICATION, baseUrl);
        context.put(APPLICATION_NAME, applicationName);
        velocityEngine.mergeTemplate(EMAIL_TEMP_PATH + account.getLanguage() + "/password.vm", UTF_8, context, stringWriter);
        String message = stringWriter.toString();
        LOG.info(url.toString());
        return mailer.sendMail(MailMail.OTHER, account.getEmail(), subject, message, resourceSrv.getBasicResourceMap());
    }

    public boolean sendInvite(String email, Theme theme) {
        String baseUrl = appSrv.getProperty(AppService.URL);
        String subject = msg.getMessage("panel.invite.subject", new Object[]{applicationName}, Utils.getDefaultLocale());
        StringWriter stringWriter = new StringWriter();
        VelocityContext context = new VelocityContext();
        context.put(APPLICATION, baseUrl);
        context.put(APPLICATION_NAME, applicationName);
        context.put(CUR_ACCOUNT, Utils.getCurrentAccount());
        context.put(THEME, theme);
        velocityEngine.mergeTemplate(EMAIL_TEMP_PATH + Utils.getDefaultLocale() + "/invite.vm", UTF_8, context, stringWriter);
        String message = stringWriter.toString();
        Map<String, Resource> resources = resourceSrv.getBasicResourceMap();
        resources.put(AVATAR, resourceSrv.getUserAvatar());
        return mailer.sendMail(MailMail.REGISTER, email, subject, message, resources);
    }

    /**
     * Fetches account once more from database to ensure that security principal was not tampered with, and verifies if entered password is valid
     *
     * @param account  user to be checked for valid password
     * @param password user password which will be matched with encoded DB password
     * @return
     */
    public boolean verifyPassword(Account account, String password) {
        Account dbAccount = findById(account.getId());
        return passwordEncoder.matches(password, dbAccount.getPassword());
    }
}
