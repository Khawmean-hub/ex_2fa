package com.beple.ex_2fa.service.user;

import com.beple.ex_2fa.payload.BaseResponse;
import com.beple.ex_2fa.payload.user.ResetPasswordReq;
import com.beple.ex_2fa.payload.user.UserUpdateReq;
import com.google.zxing.WriterException;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public interface UserService {
    BaseResponse update(UserUpdateReq req);
    BaseResponse resetPassword(ResetPasswordReq req);
    BaseResponse get2fa() throws IOException, WriterException;
    BaseResponse enable2fa(String code) throws IOException;
    BaseResponse disable2fa(String code) ;
}
