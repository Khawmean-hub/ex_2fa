package com.beple.ex_2fa.payload.auth;

import com.beple.ex_2fa.payload.user.UserRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class LoginRes {
    private AuthenticationResponse token;
    private UserRes user;
    private Object companies;
}
