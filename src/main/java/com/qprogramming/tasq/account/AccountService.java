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
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.*;

/**
 * @author romanjak
 * @date 21 maj 2014
 */
@Service
public class AccountService {

    public static final String CUR_ACCOUNT = "curAccount";
    public static final String THEME = "theme";
    public static final String AVATAR = "avatar";
    private static final String APPLICATION = "application";
    private static final String LINK = "link";
    private static final String APPLICATION_NAME = "applicationName";
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

    public boolean sendConfirmationLink(Account account) {
        String baseUrl = appSrv.getProperty(AppService.URL);
        String confirmlink = baseUrl + "/confirm?id=" + account.getUuid();
        String subject = msg.getMessage("signup.register", new Object[]{applicationName}, Utils.getDefaultLocale());
        Map<String, Object> model = new HashMap<>();
        model.put(ACCOUNT, account);
        model.put(LINK, confirmlink);
        model.put(APPLICATION, baseUrl);
        model.put(APPLICATION_NAME, applicationName);
        String message = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                "email/" + Utils.getDefaultLocale() + "/register.vm", "UTF-8", model);
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
        Map<String, Object> model = new HashMap<>();
        model.put(ACCOUNT, account);
        model.put(LINK, url);
        model.put(APPLICATION_NAME, applicationName);
        model.put(APPLICATION, baseUrl);
        String message = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                "email/" + account.getLanguage() + "/password.vm", "UTF-8", model);
        LOG.info(url.toString());
        return mailer.sendMail(MailMail.OTHER, account.getEmail(), subject, message, resourceSrv.getBasicResourceMap());
    }

    public boolean sendInvite(String email, Theme theme) {
        String baseUrl = appSrv.getProperty(AppService.URL);
        String subject = msg.getMessage("panel.invite.subject", new Object[]{applicationName}, Utils.getDefaultLocale());
        Map<String, Object> model = new HashMap<>();
        model.put(APPLICATION, baseUrl);
        model.put(APPLICATION_NAME, applicationName);
        model.put(CUR_ACCOUNT, Utils.getCurrentAccount());
        model.put(THEME, theme);
        String message = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                "email/" + Utils.getDefaultLocale() + "/invite.vm", "UTF-8", model);
        Map<String, Resource> resources = resourceSrv.getBasicResourceMap();
        resources.put(AVATAR, resourceSrv.getUserAvatar());
        return mailer.sendMail(MailMail.REGISTER, email, subject, message, resources);
    }

}
