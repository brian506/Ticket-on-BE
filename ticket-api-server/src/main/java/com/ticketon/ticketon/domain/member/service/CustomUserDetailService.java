package com.ticketon.ticketon.domain.member.service;

import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findByEmail(email), "[" + email + "] email에 해당하는 member를 찾을 수 없습니다.");
        return new CustomUserDetails(member);
    }
}