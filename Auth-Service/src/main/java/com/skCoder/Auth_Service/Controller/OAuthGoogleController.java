package com.skCoder.Auth_Service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skCoder.Auth_Service.Models.User;
import com.skCoder.Auth_Service.Service.AuthService;
import com.skCoder.Auth_Service.Service.JwtUtil;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/auth/google")
public class OAuthGoogleController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${frontend.redirect.uri}")
    private String frontendRedirectUri;

    @Autowired
    AuthService authService;
    
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/login")
    public Map<String, String> getGoogleLoginUrl() {
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid%20email%20profile";
        return Map.of("url", googleAuthUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> googleCallback(@RequestParam String code) {
        try {
            System.out.println("Received Google code: " + code);

            String accessToken = fetchAccessToken(code);
            if (accessToken == null) {
                System.out.println("Failed to retrieve access token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println("Google Access Token: " + accessToken);

            Map<String, Object> userInfo = fetchGoogleUserInfo(accessToken);
            if (userInfo == null || !userInfo.containsKey("email")) {
                System.out.println("Failed to fetch user info.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println("Google User Info: " + userInfo);

            String email = (String) userInfo.get("email");
            User user = authService.registerOauthUser(email, "GOOGLE");

            String jwt = JwtUtil.generateToken(email, user.getRoles(), user.getId());
            System.out.println("Generated JWT: " + jwt);

            URI redirectUri = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                    .queryParam("token", jwt)
                    .queryParam("username", email)
                    .build()
                    .toUri();

            System.out.println("Redirecting to: " + redirectUri);

            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String fetchAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                      "&client_secret=" + clientSecret +
                      "&code=" + code +
                      "&redirect_uri=" + redirectUri +
                      "&grant_type=authorization_code";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        System.out.println("Google Token Response: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonResponse = objectMapper.readValue(response.getBody(), Map.class);

                return (String) jsonResponse.get("access_token");
            } catch (Exception e) {
                System.out.println("Error parsing Google token response: " + e.getMessage());
            }
        }
        return null;
    }

    private Map<String, Object> fetchGoogleUserInfo(String accessToken) {
        String userUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userUrl, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }
}
