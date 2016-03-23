package com.qprogramming.tasq.account;

import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.error.TasqException;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.manage.Theme;
import com.qprogramming.tasq.manage.ThemeService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@Secured("ROLE_USER")
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);
    private static final String AVATAR_DIR = "avatar";
    private static final String PNG = ".png";
    private AccountService accountSrv;
    private ProjectService projSrv;
    private SessionLocaleResolver localeResolver;
    private MessageSource msg;
    private SessionRegistry sessionRegistry;
    private ThemeService themeSrv;
    private AppService appSrv;
    @Autowired
    public AccountController(AccountService accountSrv, ProjectService projSrv, MessageSource msg,
                             SessionLocaleResolver localeResolver, SessionRegistry sessionRegistry, ThemeService themeSrv,
                             AppService appSrv) {
        this.accountSrv = accountSrv;
        this.projSrv = projSrv;
        this.msg = msg;
        this.localeResolver = localeResolver;
        this.sessionRegistry = sessionRegistry;
        this.themeSrv = themeSrv;
        this.appSrv = appSrv;
    }

    @RequestMapping(value = "settings", method = RequestMethod.GET)
    public String settings(Model model) {
        model.addAttribute("themes", themeSrv.findAll());
        return "user/settings";
    }

    @Transactional(rollbackFor = {TasqException.class})
    @RequestMapping(value = "settings", method = RequestMethod.POST)
    public String saveSettings(@RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "emails", required = false) String emails,
                               @RequestParam(value = "language", required = false) String language,
                               @RequestParam(value = "theme", required = false) Long themeID, RedirectAttributes ra,
                               HttpServletRequest request, HttpServletResponse response) {
        Account account = Utils.getCurrentAccount();
        if (avatarFile.getSize() != 0) {
            File file = new File(getAvatar(account.getId()));
            try {
                FileUtils.writeByteArrayToFile(file, avatarFile.getBytes());
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        account.setLanguage(language);
        localeResolver.setLocale(request, response, new Locale(language));
        account.setEmail_notifications(Boolean.parseBoolean(emails));
        Theme theme = themeSrv.findById(themeID);
        account.setTheme(theme);
        String message = "";
        if (email != null && email != "" && !account.getEmail().equals(email)) {
            account.setEmail(email);
            account.setConfirmed(false);
            if (!accountSrv.sendConfirmationLink(account)) {
                throw new TasqException(msg.getMessage("error.email.sending", null, Utils.getCurrentLocale()));
            }
        }
        accountSrv.update(account);
        MessageHelper.addSuccessAttribute(ra,
                msg.getMessage("panel.saved", null, Utils.getCurrentLocale()) + ". " + message);
        return "redirect:/settings";
    }

    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public String getUser(@PathVariable(value = "username") String username, Model model, RedirectAttributes ra) {
        Account account = accountSrv.findByUsername(username);
        if (account == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("error.noUser", null, Utils.getCurrentLocale()));
            return "redirect:/users";
        }
        List<Object> principals = sessionRegistry.getAllPrincipals();
        DisplayAccount dispAccount = accountWithSession(principals, account);
        model.addAttribute("projects", projSrv.findAllByUser(account.getId()));
        model.addAttribute("account", dispAccount);
        return "user/details";
    }

    @RequestMapping(value = "/getAccounts", method = RequestMethod.GET)
    public
    @ResponseBody
    List<DisplayAccount> listAccounts(@RequestParam String term, HttpServletResponse response) {
        response.setContentType("application/json");
        List<Account> accounts = new LinkedList<Account>();
        if (term == null) {
            accounts = accountSrv.findAll();
        } else {
            accounts = accountSrv.findByNameSurnameContaining(term);
        }
        List<DisplayAccount> result = new ArrayList<DisplayAccount>();
        for (Account account : accounts) {
            DisplayAccount d_account = new DisplayAccount(account);
            result.add(d_account);
        }
        return result;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public
    @ResponseBody
    Page<DisplayAccount> listUsers(@RequestParam(required = false) String term) {
//    Page<DisplayAccount> listUsers(@RequestParam(required = false) String term,
//                                   @PageableDefault(size = 25, page = 0, sort = "surname", direction = Direction.ASC) Pageable p) {
        Page p = new PageImpl(null);
        Page<Account> page;
        if (term != null) {
            page = accountSrv.findByNameSurnameContaining(term, p);
        } else {
            page = accountSrv.findAll(p);
        }
        List<DisplayAccount> list = new LinkedList<DisplayAccount>();
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Account account : page) {
            DisplayAccount dispAccount = accountWithSession(principals, account);
            list.add(dispAccount);
        }
        Page<DisplayAccount> result = new PageImpl<DisplayAccount>(list, p, page.getTotalElements());
        return result;
    }

    @RequestMapping(value = "/project/participants", method = RequestMethod.GET)
    public
    @ResponseBody
    Page<DisplayAccount> listParticipants(@RequestParam(required = false) String term,
                                          @RequestParam String projId,
                                          @PageableDefault(size = 25, page = 0, sort = "surname", direction = Direction.ASC) Pageable p) {

        Project project = projSrv.findByProjectId(projId);
        if (project == null) {
            try {
                Long projectID = Long.valueOf(projId);
                project = projSrv.findById(projectID);
            } catch (NumberFormatException e) {
                LOG.error(e.getMessage());
            }
        }
        Set<Account> allParticipants = project.getParticipants();
        List<Object> principals = sessionRegistry.getAllPrincipals();
        List<DisplayAccount> participants = new ArrayList<DisplayAccount>();
        for (Account account : allParticipants) {
            if (term == null) {
                participants.add(accountWithSession(principals, account));
            } else {
                if (StringUtils.containsIgnoreCase(account.toString(), term)) {
                    participants.add(accountWithSession(principals, account));
                }
            }
        }
        int totalParticipants = participants.size();
        if (participants.size() > p.getPageSize()) {
            participants = participants.subList(p.getOffset(), p.getOffset() + p.getPageSize());
        }
        Page<DisplayAccount> result = new PageImpl<DisplayAccount>(participants, p, totalParticipants);
        return result;
    }

    private DisplayAccount accountWithSession(List<Object> principals, Account account) {
        DisplayAccount sAccount = new DisplayAccount(account);
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(account, false);
        if (!sessions.isEmpty() && principals.contains(account)) {
            sAccount.setOnline(true);
        }
        return sAccount;
    }

    @RequestMapping(value = "/user/{username}/reset-avatar", method = RequestMethod.GET)
    public String resetAvatar(@PathVariable(value = "username") String username, HttpServletRequest request,
                              RedirectAttributes ra) {
        if (!Roles.isAdmin()) {
            throw new TasqAuthException(msg);
        }
        Account account = accountSrv.findByUsername(username);
        if (account == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("error.noUser", null, Utils.getCurrentLocale()));
        } else {
            HttpSession session = request.getSession();
            ServletContext sc = session.getServletContext();
            File userAvatar = new File(getAvatar(account.getId()));
            Utils.copyFile(sc, "/resources/img/avatar.png", userAvatar);
            MessageHelper.addSuccessAttribute(ra, msg.getMessage("user.reset.success", null, Utils.getCurrentLocale()));
        }
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = "/emailResend", method = RequestMethod.GET)
    public String resendEmail(RedirectAttributes ra) {
        Account account = Utils.getCurrentAccount();
        if (!accountSrv.sendConfirmationLink(account)) {
            throw new TasqException(msg.getMessage("error.email.sending", null, Utils.getCurrentLocale()));
        }
        MessageHelper.addSuccessAttribute(ra, msg.getMessage("panel.emails.resend.sent",
                new Object[]{account.getEmail()}, Utils.getCurrentLocale()));
        return "redirect:/settings";
    }

    @RequestMapping(value = "role", method = RequestMethod.POST)
    @ResponseBody
    public ResultData setRole(@RequestParam(value = "id") Long id, @RequestParam(value = "role") Roles role) {
        Account account = accountSrv.findById(id);
        if (account != null) {
            // check if not admin or user
            List<Account> admins = accountSrv.findAdmins();
            if (account.getRole().equals(Roles.ROLE_ADMIN) && admins.size() == 1) {
                return new ResultData(ResultData.ERROR,
                        msg.getMessage("role.last.admin", null, Utils.getCurrentLocale()));
            } else {
                String rolemsg = msg.getMessage(role.getCode(), null, Utils.getCurrentLocale());
                account.setRole(role);
                accountSrv.update(account);
                String resultMsg = msg.getMessage("role.change.succes", new Object[]{account.toString(), rolemsg},
                        Utils.getCurrentLocale());
                return new ResultData(ResultData.OK, resultMsg);
            }
        }
        return null;
    }

    /**
     * Sends email with invite to indicated email
     *
     * @param email
     * @param request
     * @param ra
     * @return
     */
    @RequestMapping(value = "/inviteUsers", method = RequestMethod.GET)
    public String sendInvite(@RequestParam(value = "email") String email, HttpServletRequest request,
                             RedirectAttributes ra) {
        if (!accountSrv.sendInvite(email, themeSrv.getDefault())) {
            throw new TasqException(msg.getMessage("error.email.sending", null, Utils.getCurrentLocale()));
        }
        MessageHelper.addSuccessAttribute(ra,
                msg.getMessage("panel.invite.sent", new Object[]{email}, Utils.getCurrentLocale()));
        return "redirect:" + request.getHeader("Referer");
    }

    private String getAvatarDir() {
        return appSrv.getProperty(AppService.TASQROOTDIR) + File.separator + AVATAR_DIR + File.separator;
    }

    private String getAvatar(Long id) {
        return getAvatarDir() + id + PNG;
    }
}
