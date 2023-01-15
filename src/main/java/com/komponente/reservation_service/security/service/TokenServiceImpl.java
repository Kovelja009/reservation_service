package com.komponente.reservation_service.security.service;

import com.komponente.reservation_service.exceptions.ForbiddenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    @Override
    public String generate(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Override
    public Claims parseToken(String jwt) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
        return claims;
    }

    @Override
    public Long getIdFromToken(String authorization) {
        Claims claims;
        try {
            String [] split = authorization.split(" ");
            if(!split[0].equals("Bearer"))
                throw new IllegalArgumentException("Token must start with Bearer");
            String jwt = split[1];
            claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            throw new ForbiddenException("Token is not valid");
        }
        return Long.parseLong(claims.get("id", Integer.class).toString());
    }
}
