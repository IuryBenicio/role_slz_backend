package com.example.roleslz_backend.Tables.users.services;

import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    public UserEntity getUserLogado(){
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
