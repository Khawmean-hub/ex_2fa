package com.beple.ex_2fa.payload.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TwoFactorRes {
    private String key;
    private byte[] qrCode;
}
