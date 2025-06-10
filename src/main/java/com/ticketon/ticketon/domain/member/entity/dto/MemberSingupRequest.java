package com.ticketon.ticketon.domain.member.entity.dto;

import com.ticketon.ticketon.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
public class MemberSingupRequest {
    private String email;
    private String password;

    public Member toEntity(BCryptPasswordEncoder encoder) {
        return Member.builder()
                .email(email)
                .password(encoder.encode(password))
                .build();
    }
}
