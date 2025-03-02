package com.careline.interview.test.component;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.security.auth.message.AuthException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtils implements Serializable {

    private static final long EXPIRATION_TIME = 86400 * 1000 * 7; // 期限七天

    private static final String SECRET = "interview";

    public static String generateToken(int member_id,String name,String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("member_id", member_id);
        claims.put("name", name);
        claims.put("email", email);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(Instant.now().toEpochMilli() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 驗證JWT
     */
    public static void validateToken(String token) throws AuthException {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token);
        } catch (SignatureException e) {
            throw new AuthException("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            throw new AuthException("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            throw new AuthException("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            throw new AuthException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new AuthException("JWT token compact of handler are invalid");
        }
    }
    public static Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    public static Date getExpirationDateFromToken(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration;
    }

    private static Claims getClaimsFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public static String getMemberIdFromToken(String token) {
        String member_id = getClaimsFromToken(token).get("member_id").toString();
        return member_id;
    }
    public static String getEmailFromToken(String token) {
        String email = getClaimsFromToken(token).get("email").toString();
        return email;
    }
}
