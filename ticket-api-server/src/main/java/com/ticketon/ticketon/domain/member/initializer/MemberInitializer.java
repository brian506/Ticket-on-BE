package com.ticketon.ticketon.domain.member.initializer;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (memberRepository.findByEmail("user1@example.com").isPresent()) {
            return;
        }
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 100_000; i++) {
            String email = "user" + i + "@example.com";
            String password = passwordEncoder.encode("1234");
            members.add(Member.builder()
                    .email(email)
                    .password(password)
                    .build());
            if (i % 1000 == 0) {
                memberRepository.saveAll(members);
                members.clear();
            }
        }

        // 남은 사용자 저장
        if (!members.isEmpty()) {
            memberRepository.saveAll(members);
        }

    }
}

