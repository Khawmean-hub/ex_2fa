package com.beple.ex_2fa.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.beple.ex_2fa.enums.Permission.*;

@RequiredArgsConstructor
public enum Role {
    SYSTEM_ADMIN(
            Set.of(
                    SYSTEM_ADMIN_CREATE,
                    SYSTEM_ADMIN_READ,
                    SYSTEM_ADMIN_UPDATE,
                    SYSTEM_ADMIN_DELETE,
                    COMPANY_ADMIN_CREATE,
                    COMPANY_ADMIN_READ,
                    COMPANY_ADMIN_UPDATE,
                    COMPANY_ADMIN_DELETE,
                    AUTHOR_CREATE,
                    AUTHOR_READ,
                    AUTHOR_UPDATE,
                    AUTHOR_DELETE
            )
    ),
    COMPANY_ADMIN(
            Set.of(
                    COMPANY_ADMIN_READ,
                    COMPANY_ADMIN_UPDATE,
                    COMPANY_ADMIN_DELETE,
                    COMPANY_ADMIN_CREATE,
                    AUTHOR_CREATE,
                    AUTHOR_READ,
                    AUTHOR_UPDATE,
                    AUTHOR_DELETE
            )
    ),
    AUTHOR(
            Set.of(
                    AUTHOR_READ,
                    AUTHOR_UPDATE,
                    AUTHOR_DELETE,
                    AUTHOR_CREATE
            )
    ),
    USER(Set.of()),
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
