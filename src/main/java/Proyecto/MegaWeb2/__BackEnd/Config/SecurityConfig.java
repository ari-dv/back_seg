package Proyecto.MegaWeb2.__BackEnd.Config;

import Proyecto.MegaWeb2.__BackEnd.Security.JwtAuthenticationFilter;
import Proyecto.MegaWeb2.__BackEnd.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:8080",
                "https://megayuntas.amazoncode.dev"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // 🔓 AUTH (SIN JWT)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/verify-2fa").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/restablecer-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/usuarios").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/auth/generate-qr/**").permitAll()

                // 🔓 ENDPOINTS PÚBLICOS
                .requestMatchers("/estilos.css", "/javascript.js", "/img/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/test/enviar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/publicaciones").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/nosotros").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/busquedas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/consultas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/clientes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/comentarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/{id}").permitAll()

                // 🔐 TODO LO DEMÁS REQUIERE JWT
                .anyRequest().authenticated()
            )

            // 🔐 FILTRO JWT
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
