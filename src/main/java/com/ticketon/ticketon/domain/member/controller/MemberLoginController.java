package com.ticketon.ticketon.domain.member.controller;

import com.ticketon.ticketon.domain.member.entity.dto.MemberSingupRequest;
import com.ticketon.ticketon.domain.member.service.MemberService;
import com.ticketon.ticketon.global.constants.Urls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberLoginController {

    private final MemberService memberService;

    // JSON 요청 처리
    // !! 테스트 메서드 !!
    @PostMapping(value = Urls.SIGN_UP, consumes = "application/json")
    public String signupJson(@RequestBody MemberSingupRequest request) {
        memberService.save(request);
        return "redirect:/login";
    }

    // form-urlencoded 요청 처리
    // !! 테스트 메서드 !!
    @PostMapping(value = Urls.SIGN_UP, consumes = "application/x-www-form-urlencoded")
    public String signupForm(@ModelAttribute MemberSingupRequest request) {
        memberService.save(request);
        return "redirect:/login";
    }

    @GetMapping(Urls.SIGN_UP)
    public String signupPage() {
        return "login/signup";
    }
}
