package com.example.projetoGerenciamento.service;

import com.example.projetoGerenciamento.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    // 30 days
    private final long EXPIRATION = 1000L * 60 * 60 * 24 * 30;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    // generates the token with the user's email
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    // extracts the email from the token
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // validates if the token belongs to the user and has not expired
    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        return email.equals(user.getEmail()) && !isTokenExpired(token);
    }

    // checks if the token expiration date is before the current date
    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    // parses the token, verifies the signature and returns the payload (Claims)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}