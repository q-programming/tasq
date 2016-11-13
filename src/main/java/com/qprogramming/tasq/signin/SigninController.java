package com.qprogramming.tasq.signin;

import com.qprogramming.tasq.support.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SigninController {

    @RequestMapping(value = "signin")
    public void signin(HttpServletRequest request) {
        Utils.forceLogout(request);
    }
}
