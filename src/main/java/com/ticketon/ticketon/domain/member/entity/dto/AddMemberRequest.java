package com.ticketon.ticketon.domain.member.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMemberRequest {
    private String email;
    private String password;
}
