package com.beple.ex_2fa.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    SYSTEM_ADMIN_READ("system_admin:read"),
    SYSTEM_ADMIN_UPDATE("system_admin:update"),
    SYSTEM_ADMIN_DELETE("system_admin:delete"),
    SYSTEM_ADMIN_CREATE("system_admin:create"),
    COMPANY_ADMIN_READ("company_admin:read"),
    COMPANY_ADMIN_UPDATE("company_admin:update"),
    COMPANY_ADMIN_DELETE("company_admin:delete"),
    COMPANY_ADMIN_CREATE("company_admin:create"),
    AUTHOR_READ("author:read"),
    AUTHOR_UPDATE("author:update"),
    AUTHOR_DELETE("author:delete"),
    AUTHOR_CREATE("author:create"),
    ;

    @Getter
    private final String permission;
}
