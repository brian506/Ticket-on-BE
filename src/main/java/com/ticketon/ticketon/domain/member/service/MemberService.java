package com.ticketon.ticketon.domain.member.service;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.entity.dto.MemberSingupRequest;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public long save(MemberSingupRequest dto) {
        return memberRepository.save(Member.builder()
                        .email(dto.getEmail())
                        .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }


}
