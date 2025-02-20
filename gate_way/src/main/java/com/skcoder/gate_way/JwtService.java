package com.skcoder.gate_way;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
	 private static final String SECRET_KEY = "supersecretkey1234567890supersecretkey";
    private final SecretKey secretKey =Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true; 
        } catch (Exception e) {
            return false; 
        }
    }
}
