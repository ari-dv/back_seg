package Proyecto.MegaWeb2.__BackEnd.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.startsWith("/api/auth/");
}


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                if (jwtUtil.isTokenValid(jwt)) {
                    String username = jwtUtil.extractUsername(jwt);
                    Integer idRol = jwtUtil.extractIdRol(jwt);
                    Integer idUsuario = jwtUtil.extractIdUsuario(jwt);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + idRol))
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    request.setAttribute("idRol", idRol);
                    request.setAttribute("idUsuario", idUsuario);
                }
            } catch (Exception e) {
                // ❌ No bloqueamos el request, solo logueamos
                System.out.println("JWT inválido o ausente: " + e.getMessage());
            }
        }

        // 🔹 Siempre continuar con la chain, aunque no haya JWT
        filterChain.doFilter(request, response);
    }
}