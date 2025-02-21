package com.skCoder.Auth_Service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.skCoder.Auth_Service.Models.LoginRequest;
import com.skCoder.Auth_Service.Models.RegisterRequest;
import com.skCoder.Auth_Service.Service.AuthService;
import com.skCoder.Auth_Service.Service.JwtService;

@RestController
@RequestMapping("/authv1")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request.getUsername(), request.getPassword(), request.getRole());
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token) {
    	jwtService.printTokenInfo(token);
        return jwtService.validateToken(token);
    }
}
