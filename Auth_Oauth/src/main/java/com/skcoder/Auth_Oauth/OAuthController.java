package com.skcoder.Auth_Oauth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class OAuthController {

    @GetMapping("/auth/github/login")
    public void redirectToGitHub(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/github"); // Redirects user to GitHub login
    }

    @GetMapping("/auth/github/success")
    public String githubSuccess(OAuth2AuthenticationToken authentication) {
        OAuth2User user = authentication.getPrincipal();
        Map<String, Object> attributes = user.getAttributes();
        String username = (String) attributes.get("login");  // GitHub username

        // Generate JWT Token (Assuming you have a JwtUtil class)
        String jwt = JwtUtil.generateToken(username, "USER"); // Default role: USER

        return "Your JWT Token: " + jwt;
    }
}
