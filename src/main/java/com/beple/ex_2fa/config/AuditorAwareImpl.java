package com.beple.ex_2fa.config;

import com.beple.ex_2fa.utils.helper.AuthHelper;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if (AuthHelper.getUser()==null) {
            return Optional.of("system");
        }
        return Optional.ofNullable(AuthHelper.getUsername());
    }
}