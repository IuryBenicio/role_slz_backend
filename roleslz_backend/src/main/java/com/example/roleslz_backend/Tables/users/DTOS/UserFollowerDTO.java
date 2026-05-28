package com.example.roleslz_backend.Tables.users.DTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserFollowerDTO {
    @NotNull
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
}
