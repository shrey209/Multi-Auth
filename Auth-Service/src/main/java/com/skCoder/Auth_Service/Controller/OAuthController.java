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
@RequestMapping("/auth/github")
public class OAuthController {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Value("${frontend.redirect.uri}")
    private String frontendRedirectUri;

    @Autowired
    AuthService authService;
    
    private final RestTemplate restTemplate = new RestTemplate();

    
    @GetMapping("/login")
    public Map<String, String> getGitHubLoginUrl() {
        String githubAuthUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=user";
        return Map.of("url", githubAuthUrl);
    }

   
    @GetMapping("/callback")
    public ResponseEntity<Void> githubCallback(@RequestParam String code) {
        try {
            System.out.println("Received GitHub code: " + code);

            String accessToken = fetchAccessToken(code);
            if (accessToken == null) {
                System.out.println("Failed to retrieve access token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println("GitHub Access Token: " + accessToken);

        
            Map<String, Object> userInfo = fetchGitHubUserInfo(accessToken);
            if (userInfo == null || !userInfo.containsKey("id")) {
                System.out.println("Failed to fetch user info.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println("GitHub User Info: " + userInfo);

            String githubId = userInfo.get("id").toString();
            String username = (String) userInfo.get("login");
            User user=authService.registerOauthUser(username, "GITHUB");
            
            String jwt = JwtUtil.generateToken(username,user.getRoles(),user.getId());
            System.out.println("Generated JWT: " + jwt);

 
            URI redirectUri = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                    .queryParam("token", jwt)
                    .queryParam("username", username)
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
        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String body = "client_id=" + clientId + 
                      "&client_secret=" + clientSecret + 
                      "&code=" + code + 
                      "&redirect_uri=" + redirectUri;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        System.out.println("GitHub Token Response: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonResponse = objectMapper.readValue(response.getBody(), Map.class);

                return (String) jsonResponse.get("access_token");
            } catch (Exception e) {
                System.out.println("Error parsing GitHub token response: " + e.getMessage());
            }
        }
        return null;
    }



    private Map<String, Object> fetchGitHubUserInfo(String accessToken) {
        String userUrl = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); 
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userUrl, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }
}
