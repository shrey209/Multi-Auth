package com.skCoder.Auth_Service.Models;

import com.skCoder.Auth_Service.Models.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterRequest {
    private String username;
    private String password;
   private String gmail;
}
