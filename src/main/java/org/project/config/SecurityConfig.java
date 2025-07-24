package org.project.config;

import org.project.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                // TODO: Cần phải cấu hình lại để phù hợp với các API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/auth-view/**",
                                "/auth/google",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/vendor/**",
                                "/forgotPassword/**",
                                "/webjars/**",
                                "/favicon.ico",
                                "frontend/assets/**",
                                "dashboard-staff-test/assets/**",
                                "templates_storage/assets/**",
                                "/",
                                "/home",
                                "/about-us",
                                "/hospital/**",
                                "/department/**",
                                "/doctor/**",
                                "/service/**",
                                "/templates/**"
                        ).permitAll()

                        // TODO: Sửa lại đường dẫn để phù hợp với các API
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/patient/**").hasRole("PATIENT")
                        .requestMatchers("/staff/pharmacy/**").hasRole("STAFF_PHARMACIST")
                        .requestMatchers("/staff/lab/**").hasRole("STAFF_TECHNICIAN")
                        .requestMatchers("/lab/**").hasRole("STAFF_TECHNICIAN")
                        .requestMatchers("/staff/schedule/**").hasRole("STAFF_SCHEDULING_COORDINATOR")
                        .requestMatchers("/staff/inventory/**").hasRole("STAFF_INVENTORY_MANAGER")
                        .requestMatchers("/staff/lab-receive/**").hasRole("STAFF_LAB_RECEIVER")
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
                    // Xử lý lỗi 401: chưa đăng nhập
                    response.sendRedirect("/auth-view/login");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // Xử lý lỗi 403: không đủ quyền
                    response.sendRedirect("/home");
                })
        );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
