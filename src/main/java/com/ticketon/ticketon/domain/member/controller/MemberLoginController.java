package com.ticketon.ticketon.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberLoginController {

    @GetMapping("/signup")
    public String signupPage() {
        return "login/signup";
    }

    @GetMapping("/success")
    public String loginSuccess() {
        return "login/success";
    }

}
