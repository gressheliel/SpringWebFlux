package com.elhg.helpers;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtHelper {

    @Value("${jwt.secret:mySecretKey12345678901234567890123456789012}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    public String generateJwt(String username, List<String> roles){
        final Date now = new Date();
        final Date exp = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .claims(Map.of("roles", roles))
                .signWith(getSecretKey())
                .compact();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwt(String token) {
        Claims claims = getClaimsFromJwt(token);
        return claims.get("roles", List.class);
    }

    public boolean validateJwt(String jwt){
        try{
            final Claims claims =  getClaimsFromJwt(jwt);
            final Date expirationDate = claims.getExpiration();

            return expirationDate.after(new Date());
        }catch (Exception e){
            return false;
        }
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = getClaimsFromJwt(token);
        return claims.getSubject();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Claims getClaimsFromJwt(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
