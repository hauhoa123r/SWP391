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
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
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


                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .requestMatchers("/patient/**").hasRole("PATIENT")
                        .requestMatchers("/staff/pharmacy/**").hasRole("STAFF_PHARMACIST")
                        .requestMatchers("/staff/lab/**").hasRole("STAFF_TECHNICIAN")
                        .requestMatchers("/abc/**").hasRole("STAFF_TECHNICIAN")
                        .requestMatchers("/staff/schedule/**").hasRole("STAFF_SCHEDULING_COORDINATOR")
                        .requestMatchers("/staff/inventory/**").hasRole("STAFF_INVENTORY_MANAGER")
                        .requestMatchers("/staff/lab-receive/**").hasRole("STAFF_LAB_RECEIVER")
                        .requestMatchers("/lab/**").hasRole("STAFF_DOCTOR")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/auth-view/login");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {

                    response.sendRedirect("/auth-view/login");
                })
        );

        return http.build();
    }

}


