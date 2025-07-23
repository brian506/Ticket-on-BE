package com.ticketon.ticketon.domain.member.controller;

import com.ticket.dto.SuccessResponse;
import com.ticketon.ticketon.domain.member.dto.MemberSingUpRequest;
import com.ticketon.ticketon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse> signup(@RequestBody MemberSingUpRequest request) {
        memberService.save(request);
        SuccessResponse response = new SuccessResponse(true, "회원가입 성공", null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}