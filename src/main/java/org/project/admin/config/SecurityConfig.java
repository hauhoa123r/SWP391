package org.project.admin.config;

import org.project.config.JWTAuthFilter;
import org.project.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Sử dụng cấu hình CORS từ bean
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/auth-view/**",
                                "/auth/google",
                                "/assets/**",
                                "/frontend/assets/**",
                                "/dashboard-staff-test/assets/**",
                                "/templates_storage/assets/**",
                                "/",
                                "/home",
                                "/about-us",
                                "/hospital/**",
                                "/department/**",
                                "/doctor/**",
                                "/service/**",
                                "/templates/**",
                                "/patient/appointment-result/**",
                                "/api/appointments/in-progress/**",
                                "/api/medical-record/**",
                                "/api/medical-profile/**",
                                "/api/medical-record-symptom/**",
                                "/api/vital/**",
                                "/api/respiratory/**",
                                "/api/cardiac/**",
                                "/api/neurologic/**",
                                "/api/gastro/**",
                                "/api/genitourinary/**",
                                "/api/musculoskeletal/**",
                                "/api/dermatologic/**",
                                "/api/notes/**",
                                "/api/test-request/**"
                        ).permitAll()
                        .requestMatchers("/api/**").hasAnyRole("ADMIN","STAFF_DOCTOR")
                        .requestMatchers("/patient/**").hasRole("PATIENT")
                        .requestMatchers("/staff/doctor/**").hasRole("STAFF_DOCTOR")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api")) {
                        response.setContentType("application/json");
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write("{\"error\":\"Unauthorized\"}");
                    } else {
                        response.sendRedirect("/auth-view/login");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getRequestURI().startsWith("/api")) {
                        response.setContentType("application/json");
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write("{\"error\":\"Forbidden\"}");
                    } else {
                        response.sendRedirect("/auth-view/login");
                    }
                }));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Disposition"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


