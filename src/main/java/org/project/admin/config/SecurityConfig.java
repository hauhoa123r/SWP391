package org.project.admin.config;

import org.project.config.JWTAuthFilter;
import org.project.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        // Định nghĩa các endpoints công khai
        String[] publicEndpoints = {
                "/auth/**",         // Login và đăng ký
                "/auth-view/**",    // Các trang view công khai
                "/auth/google",     // Google OAuth
                "/assets/**",       // Tài nguyên tĩnh như CSS, JS
                "/css/**",
                "/js/**",
                "/images/**",
                "/vendor/**",
                "/forgotPassword/**", // Endpoint quên mật khẩu
                "/webjars/**",
                "/favicon.ico",
                "frontend/assets/**",
                "dashboard-staff-test/assets/**",
                "templates_storage/assets/**",
                "/"
        };

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicEndpoints).permitAll() // Cho phép các endpoints công khai
                        .requestMatchers("/api/**").permitAll() // Các API yêu cầu không xác thực
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Chỉ Admin mới được truy cập /admin/**
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")  // Chỉ Doctor mới được truy cập /doctor/**
                        .requestMatchers("/patient/**").hasRole("PATIENT")  // Chỉ Patient mới được truy cập /patient/**
                        .requestMatchers("/staff/**").hasRole("STAFF") // Các endpoint của staff có thể được truy cập theo vai trò
                        .requestMatchers("/home").hasAnyRole("ADMIN", "DOCTOR", "PATIENT", "STAFF") // Cho phép Admin, Doctor, Patient, và Staff truy cập /home
                        .anyRequest().authenticated()  // Các request khác cần phải xác thực
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Không sử dụng session
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/404b"))

                .authenticationProvider(authenticationProvider())  // Sử dụng provider để xác thực
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Thêm filter JWT

        return http.build();
    }
}


