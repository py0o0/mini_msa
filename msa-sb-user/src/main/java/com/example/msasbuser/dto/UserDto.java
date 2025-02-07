package com.example.msasbuser.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class UserDto {
    @NotNull
    private String email;
    private String userName;
    private String password;
    private String role = "ROLE_USER";
}
