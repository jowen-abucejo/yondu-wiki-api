package com.yondu.knowledgebase.config;

import com.yondu.knowledgebase.DTO.token.TokenDTO;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Calendar;

@Component
public class TokenUtil {

    private final Logger log = LoggerFactory.getLogger(TokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION = 14 * 24 * 60 * 60 * 1000;

    public TokenDTO.Base generateToken(User user) {
        log.info("TokenUtil.generateToken()");
        log.info("user : " + user.toString());

        Calendar now = Calendar.getInstance();
        Calendar expiration = Calendar.getInstance();
        expiration.setTimeInMillis(expiration.getTimeInMillis() + EXPIRATION);

        String token = Jwts.builder()
                .setHeaderParam("email", user.getEmail())
                .setHeaderParam("username", user.getUsername())
                .setHeaderParam("id", user.getId())
                .setSubject("user")
                .setIssuer("admin")
                .setIssuedAt(now.getTime())
                .setExpiration(expiration.getTime())
                .signWith(generateSigningKey())
                .compact();

        LocalDateTime ldtIssued = Util.convertCalendarToLDT(now);
        LocalDateTime ldtExpiration = Util.convertCalendarToLDT(expiration);

        return new TokenDTO.Base(token, ldtIssued, ldtExpiration);
    }

    public Jwt readJwt(String token) throws JwtException {
        Jwt builtToken = Jwts.parserBuilder()
                .setSigningKey(generateSigningKey())
                .build()
                .parse(token);

        return builtToken;
    }

    private Key generateSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
