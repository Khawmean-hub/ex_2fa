package com.beple.ex_2fa.service;

import com.beple.ex_2fa.payload.auth.TwoFactorRes;
import com.beple.ex_2fa.utils.TwoFaceAuthUtil;
import com.beple.ex_2fa.utils.helper.AuthHelper;
import com.google.zxing.WriterException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TwoFactorAuthService {

    public TwoFactorRes get2Factor() throws IOException, WriterException {
      String secretKey      = TwoFaceAuthUtil.generateSecretKey();
      String urlCode        = TwoFaceAuthUtil.getGoogleAuthenticatorBarCode(secretKey, AuthHelper.getUsername(), "비플페이");
      byte[] qrcode         = TwoFaceAuthUtil.createQRCode(urlCode, 400, 400);

      return TwoFactorRes.builder().key(secretKey).qrCode(qrcode).build();
  }

  public boolean verify2Factor(String code) {
        return code.equals(TwoFaceAuthUtil.getTOTPCode(AuthHelper.getSecretKey()));
  }
}
