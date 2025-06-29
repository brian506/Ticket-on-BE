package com.ticketon.ticketon.domain.member.service;

import com.ticketon.ticketon.domain.member.dto.MemberSingUpRequest;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void save(final MemberSingUpRequest request) {
        memberRepository.save(request.toEntity(bCryptPasswordEncoder));
    }
}
