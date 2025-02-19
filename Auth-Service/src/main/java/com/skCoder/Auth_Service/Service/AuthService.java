package com.skCoder.Auth_Service.Service;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.skCoder.Auth_Service.Models.Role;
import com.skCoder.Auth_Service.Models.User;
import com.skCoder.Auth_Service.Repository.UserRepository;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // User Registration
    public String register(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already taken!";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setRole(role);

        userRepository.save(user);
        return "User registered successfully!";
    }

    // User Login & JWT Generation
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "Invalid credentials!";
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid credentials!";
        }

        return JwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }
}
