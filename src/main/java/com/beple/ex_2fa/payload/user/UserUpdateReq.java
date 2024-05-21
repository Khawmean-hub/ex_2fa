package com.beple.ex_2fa.payload.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserUpdateReq {
    private long id;
    private String firstname;
    private String lastname;
    private String profile;
}
