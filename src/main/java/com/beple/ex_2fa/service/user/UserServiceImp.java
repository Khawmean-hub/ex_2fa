package com.beple.ex_2fa.service.user;

import com.beple.ex_2fa.domain.user.User;
import com.beple.ex_2fa.domain.user.UserRepository;
import com.beple.ex_2fa.enums.ResponseMessage;
import com.beple.ex_2fa.exception.CustomException;
import com.beple.ex_2fa.mapper.UserMapper;
import com.beple.ex_2fa.payload.BaseResponse;
import com.beple.ex_2fa.payload.auth.TwoFactorRes;
import com.beple.ex_2fa.payload.user.ResetPasswordReq;
import com.beple.ex_2fa.payload.user.UserUpdateReq;
import com.beple.ex_2fa.service.TwoFactorAuthService;
import com.beple.ex_2fa.utils.helper.AuthHelper;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService{
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TwoFactorAuthService factorAuthService;
    @Override
    public BaseResponse update(UserUpdateReq req) {
        User user = userRepository.findById(req.getId()).orElseThrow(()-> new CustomException(ResponseMessage.USER_NOT_FOUND));
        userMapper.update(req,user);
        userRepository.save(user);
        return BaseResponse.builder().build();
    }

    @Override
    public BaseResponse resetPassword(ResetPasswordReq req) {
        User user = userRepository.findById(req.getId()).orElseThrow(()-> new CustomException(ResponseMessage.USER_NOT_FOUND));
        if(!passwordEncoder.matches(req.getOldPassword(),user.getPassword())){
            throw new CustomException(ResponseMessage.INCORRECT_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return BaseResponse.builder().build();
    }

    @Override
    public BaseResponse get2fa() throws IOException, WriterException {

        if(AuthHelper.isEnable2fa()) return BaseResponse.builder().code("9999").message("Please Disable first").build();

        TwoFactorRes res = factorAuthService.get2Factor();
        User user = AuthHelper.getUser();
        user.setScrKey(res.getKey());
        userRepository.save(user);
        return BaseResponse.builder().rec(res).build();
    }

    @Override
    public BaseResponse enable2fa(String code) {
        if(factorAuthService.verify2Factor(code)){
            User user = AuthHelper.getUser();
            user.setEnable2fa(true);
            userRepository.save(user);
            return BaseResponse.builder().build();
        }else{
            return BaseResponse.builder().code("9999").message("Invalid Code").build();
        }
    }

    @Override
    public BaseResponse disable2fa(String code) {
        if(factorAuthService.verify2Factor(code)){
            User user = AuthHelper.getUser();
            user.setEnable2fa(false);
            user.setScrKey(null);
            userRepository.save(user);
            return BaseResponse.builder().build();
        }else{
            return BaseResponse.builder().code("9999").message("Invalid Code").build();
        }
    }

    @Override
    public BaseResponse verify2fa(String code) {
        if(factorAuthService.verify2Factor(code)){
            return BaseResponse.builder().build();
        }else{
            return BaseResponse.builder().code("9999").message("Invalid Code").build();
        }
    }
}
