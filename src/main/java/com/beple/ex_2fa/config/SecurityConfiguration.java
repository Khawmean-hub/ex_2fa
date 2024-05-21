package com.beple.ex_2fa.config;

import com.beple.ex_2fa.enums.ResponseMessage;
import com.beple.ex_2fa.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static com.beple.ex_2fa.enums.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final JwtService jwtService;

    private static final List<String> PERMIT_ALL = List.of(
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/image/**",
            "/file/**",
            "/uploadFile",
            "/uploadMultiFile",
            "/api/v1/post/detail/**",
            "/api/v1/openapi/post/**"
    );
    private static final List<String> PERMIT_ALL_ROLE = List.of(
            "/api/*"
    );


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer
                                .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                PERMIT_ALL.stream().map(AntPathRequestMatcher::antMatcher)
                                        .toArray(AntPathRequestMatcher[]::new)
                        )
                        .permitAll()
                        .requestMatchers(
                                PERMIT_ALL_ROLE.stream().map(AntPathRequestMatcher::antMatcher)
                                        .toArray(AntPathRequestMatcher[]::new)
                        ).hasAnyRole(SYSTEM_ADMIN.name(), COMPANY_ADMIN.name(), AUTHOR.name())
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(this::accessDeniedHandler)
                                .authenticationEntryPoint(this::unauthorizedHandler)
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(log -> log
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );

        return http.build();
    }

    private void accessDeniedHandler(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) {
        jwtService.jwtExceptionHandler(response, ResponseMessage.FORBIDDEN);
    }

    public void unauthorizedHandler(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null ||!authHeader.startsWith("Bearer "))
            jwtService.jwtExceptionHandler(response, ResponseMessage.UNAUTHORIZED);
        else{
            jwtService.jwtExceptionHandler(response, ResponseMessage.INTERNAL_SERVER_ERROR);
            throw authException;
        }
    }
}

