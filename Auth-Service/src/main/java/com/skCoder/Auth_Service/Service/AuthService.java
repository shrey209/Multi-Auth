package com.skCoder.Auth_Service.Service;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy.Provider;
import com.skCoder.Auth_Service.Models.Role;
import com.skCoder.Auth_Service.Models.User;
import com.skCoder.Auth_Service.Repository.UserRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  
    public String register(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already taken!";
        }
Set<Role>roles=new HashSet<>();
        roles.add(role);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); 
        user.setRoles(roles);
        user.setProvider("LOCAL");
        userRepository.save(user);
        return "User registered successfully!";
    }

 
    public User registerOauthUser(String username, String provider) {
        Map<String, String> providerShortcuts = Map.of(
            "GITHUB", "gh",
            "GOOGLE", "google"
         
        );

        String providerSuffix = providerShortcuts.getOrDefault(provider.toUpperCase(), provider.toLowerCase());
        String formattedUsername = username + "-" + providerSuffix;

        Optional<User> appUser = userRepository.findByUsernameAndProvider(formattedUsername, provider);
        if (appUser.isPresent()) return appUser.get();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        User user = new User();
        user.setUsername(formattedUsername);
        user.setRoles(roles);
        user.setProvider(provider);
        userRepository.save(user);
        return user;
    }

    
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "Invalid credentials!";
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid credentials!";
        }

        return JwtUtil.generateToken(user.getUsername(), user.getRoles(),user.getId());
    }
}
