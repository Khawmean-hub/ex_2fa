package com.beple.ex_2fa.payload.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserRes {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String profile;
    private String role;
    private boolean enable2fa;
    private String status;
}
