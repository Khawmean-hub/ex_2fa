package com.beple.ex_2fa.controller;

import com.beple.ex_2fa.payload.user.ResetPasswordReq;
import com.beple.ex_2fa.payload.user.UserUpdateReq;
import com.beple.ex_2fa.service.user.UserService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    @PutMapping("/update")
    public Object update(@RequestBody UserUpdateReq req) {
        return userService.update(req);
    }

    @PutMapping("/reset-password")
    public Object resetPassword(@RequestBody ResetPasswordReq req) {
        return userService.resetPassword(req);
    }

    @GetMapping("/factor")
    public Object getTwoFactor() throws IOException, WriterException { return userService.get2fa(); }

    @PutMapping("/enable2fa")
    public Object enable2fa(@RequestParam String code) throws IOException { return userService.enable2fa(code); }

    @PutMapping("/disable2fa")
    public Object disable2fa(@RequestParam String code) { return userService.disable2fa(code); }
}
