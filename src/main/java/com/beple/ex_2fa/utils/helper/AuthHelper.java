package com.beple.ex_2fa.utils.helper;

import com.beple.ex_2fa.domain.user.User;
import com.beple.ex_2fa.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


public class AuthHelper {
    private static Authentication getAuth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getUser(){
        return (User) getAuth().getPrincipal();
    }

    public static String getUsername(){
        return getUser().getUsername();
    }

    public static String getSecretKey(){ return getUser().getScrKey(); }

    public static boolean isEnable2fa(){ return getUser().isEnable2fa(); }

    public static Role getRole(){
        return getUser().getRole();
    }

    public static long getUserId(){
        return getUser().getId();
    }

    public static boolean isUser(){
        return getRole().equals(Role.USER);
    }


    public static String getFullName(){
        return getUser().getFirstname() + " " + getUser().getLastname();
    }
    public static void reload(){
        SecurityContext context = SecurityContextHolder.getContext();
        context.getAuthentication().setAuthenticated(false);
    }
}
