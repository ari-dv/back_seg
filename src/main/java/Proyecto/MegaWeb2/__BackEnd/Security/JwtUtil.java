package Proyecto.MegaWeb2.__BackEnd.Security;

import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.time.Duration; // 👈 asegúrate de tener este import arriba

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Genera token a partir de DTO (por ejemplo, login Google)
    public Map<String, Object> generateToken(UsuarioDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("nombres", user.getNombres());
        claims.put("apellidos", user.getApellidos());
        claims.put("idRol", user.getRol());

        long now = System.currentTimeMillis();
        long expiration = now + 1000L * 60 * 1 * 6; // 6 horas

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiration))
                .signWith(SECRET_KEY)
                .compact();


        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("expiration", expiration);

        return response;
    }

// Generar token temporal para recuperación de contraseña
public String generateTemporaryToken(String email, Duration duration) {
    Instant now = Instant.now();
    return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(duration)))
            .signWith(SECRET_KEY) // 👈 usa la misma clave
            .compact();
}

// Validar token y devolver el email si es válido
public String validarYObtenerEmail(String token) {
    try {
        Claims claims = Jwts.parserBuilder() // 👈 usa parserBuilder con SECRET_KEY
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    } catch (JwtException e) {
        return null;
    }
}

    // Genera token a partir de entidad User (login tradicional)
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getEmail());
        claims.put("nombres", user.getNombres());
        claims.put("apellidos", user.getApellidos());
        claims.put("idRol", user.getIdRol());

        long now = System.currentTimeMillis();
        long expiration = now + 1000L * 60 * 1 * 6; // 6 horas

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiration))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Extrae el subject (username o email)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extrae idRol del claim
    public Integer extractIdRol(String token) {
        Object claim = extractAllClaims(token).get("idRol");
        if (claim instanceof Integer) return (Integer) claim;
        if (claim instanceof Long)    return ((Long) claim).intValue();
        return null;
    }

    // Extrae id de usuario del claim
    public Integer extractIdUsuario(String token) {
        Object claim = extractAllClaims(token).get("id");
        if (claim instanceof Integer) return (Integer) claim;
        if (claim instanceof Long)    return ((Long) claim).intValue();
        return null;
    }

    // Comprueba expiración y validez
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Parser de todos los claims
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}