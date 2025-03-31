package com.example.computerweb.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class SecurityUtils {
    public static String getPrincipal() {
        return (String) (SecurityContextHolder
                .getContext()).getAuthentication().getPrincipal();
    }


    public static List<String> getAuthorities() {
        List<String> results = new ArrayList<>();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>)(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        for (GrantedAuthority authority : authorities) {
            results.add(authority.getAuthority());
        }
        return results;
    }


//    public static String getPrincipal() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof User) {
//            return ((User) principal).getUsername(); // email đó bạn đã set
//        } else {
//            return principal.toString(); // fallback
//        }
//    }

}
