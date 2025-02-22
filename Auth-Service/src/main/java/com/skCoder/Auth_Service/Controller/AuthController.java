package com.skCoder.Auth_Service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.skCoder.Auth_Service.Models.LoginRequest;
import com.skCoder.Auth_Service.Models.RegisterRequest;
import com.skCoder.Auth_Service.Service.AuthService;
import com.skCoder.Auth_Service.Service.CaffeineCacheService;
import com.skCoder.Auth_Service.Service.EmailService;
import com.skCoder.Auth_Service.Service.JwtService;

@RestController
@RequestMapping("/authv1")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;
    

    
    
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
      String otp=   authService.register(request.getUsername(), request.getPassword(),request.getGmail());
      emailService.sendEmail(request.getGmail(), "otp verification", "Your otp is ->"+otp);  
         return "verify the otp to confirm registration";
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
    
    @GetMapping("verify")
    public ResponseEntity<?> verify(@RequestParam String otp, @RequestParam String gmail) {
        try {
            String token = authService.verifyOtp(gmail, otp);
            return ResponseEntity.ok(token);  // Return 200 with token
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    
    
}
