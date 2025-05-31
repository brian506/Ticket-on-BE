package com.ticketon.ticketon.domain.member.controller;

import com.ticketon.ticketon.domain.member.entity.dto.AddMemberRequest;
import com.ticketon.ticketon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberApiController {

    private final MemberService memberService;

    // JSON 요청 처리
    @PostMapping(value = "/signup", consumes = "application/json")
    public String signupJson(@RequestBody AddMemberRequest request) {
        memberService.save(request);
        return "redirect:/login";
    }

    // form-urlencoded 요청 처리
    @PostMapping(value = "/signup", consumes = "application/x-www-form-urlencoded")
    public String signupForm(@ModelAttribute AddMemberRequest request) {
        memberService.save(request);
        return "redirect:/login";
    }
}
