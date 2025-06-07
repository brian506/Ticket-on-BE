package com.ticketon.ticketon.domain.member.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSingupRequest {
    private String email;
    private String password;
}
