package com.ticketon.ticketon.domain.member.dto;

import com.ticketon.ticketon.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
public class MemberSingUpRequest {
    private String email;
    private String password;

    public Member toEntity(BCryptPasswordEncoder encoder) {
        return Member.builder()
                .email(email)
                .password(encoder.encode(password))
                .build();
    }
}
