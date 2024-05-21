package com.beple.ex_2fa.payload.user;

import lombok.Data;

@Data
public class ResetPasswordReq {
    private long id;
    private String oldPassword;
    private String newPassword;
}
