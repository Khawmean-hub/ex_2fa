package com.beple.ex_2fa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.beple.ex_2fa.domain.token.Token;
import com.beple.ex_2fa.domain.token.TokenRepository;
import com.beple.ex_2fa.domain.user.User;
import com.beple.ex_2fa.domain.user.UserRepository;
import com.beple.ex_2fa.enums.ResponseMessage;
import com.beple.ex_2fa.exception.CustomException;
import com.beple.ex_2fa.mapper.UserMapper;
import com.beple.ex_2fa.payload.BaseResponse;
import com.beple.ex_2fa.payload.auth.AuthenticationRequest;
import com.beple.ex_2fa.payload.auth.AuthenticationResponse;
import com.beple.ex_2fa.payload.auth.LoginRes;
import com.beple.ex_2fa.payload.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserMapper userMapper;
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public BaseResponse register(RegisterRequest request) {
    repository.findByUsername(request.getUsername()).ifPresent(user -> {
      throw new CustomException(ResponseMessage.USERNAME_ALREADY_EXISTS);
    });
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return BaseResponse.builder()
        .rec(AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .accessExpired(jwtService.getAccessExpired())
            .refreshExpired(jwtService.getRefreshExpired())
            .build())
        .build();
  }

  public BaseResponse authenticate(AuthenticationRequest request) {
    var user = repository.findByUsername(request.getUsername()).orElseThrow(()-> new CustomException(ResponseMessage.INCORRECT_USERNAME_OR_PASSWORD));
    if(!user.isEnabled()){
      throw new CustomException(ResponseMessage.USER_IS_DISABLED);
    }
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
    );
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return BaseResponse.builder()
            .rec(LoginRes.builder()
                    .token(AuthenticationResponse.builder()
                            .accessToken(jwtToken)
                            .refreshToken(refreshToken)
                            .accessExpired(jwtService.getAccessExpired())
                            .refreshExpired(jwtService.getRefreshExpired())
                            .build())
                    .user(userMapper.toRes(user))
                    .build())
            .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByUsername(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessExpired(jwtService.getAccessExpired())
                .refreshExpired(jwtService.getRefreshExpired())
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public void enable2fac(){

  }
}
