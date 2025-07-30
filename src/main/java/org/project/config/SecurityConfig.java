package org.project.config;

import org.project.security.AccountDetailsService;
import org.project.security.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private CustomOidcUserService customOidcUserService;
    private AccountDetailsService accountDetailsService;

    @Autowired
    public void setAccountDetailsService(AccountDetailsService accountDetailsService) {
        this.accountDetailsService = accountDetailsService;
    }

    @Autowired
    public void setCustomOidcUserService(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(
                                        "/admin/**"
                                ).hasRole("ADMIN")

                                .requestMatchers(
                                        "/patient/**",
                                        "/api/patient/**"
                                ).hasRole("PATIENT")

                                .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);

        httpSecurity
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint((request, response, authException) -> {
                                    // Xử lý lỗi 401: chưa đăng nhập
                                    response.sendRedirect("/login");
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    // Xử lý lỗi 403: không đủ quyền
                                    response.sendRedirect("/home");
                                })
                );

        httpSecurity
                .formLogin(
                        httpSecurityFormLoginConfigurer ->
                                httpSecurityFormLoginConfigurer
                                        .loginPage("/login")
                                        .loginProcessingUrl("/api/login")
                                        .defaultSuccessUrl("/home", true)
                                        .failureHandler((request, response, exception) -> {
                                            response.setStatus(404);
                                            response.setContentType("application/json; charset=UTF-8");
                                            response.getWriter().write("Email hoặc mật khẩu không đúng");
                                        })
                                        .permitAll())
                .oauth2Login(httpSecurityOAuth2LoginConfigurer ->
                        httpSecurityOAuth2LoginConfigurer
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.oidcUserService(customOidcUserService))
                                .failureHandler((request, response, exception) -> {
                                    request.setAttribute("toastType", "danger");
                                    request.setAttribute("toastMessage", exception.getMessage());
                                    request.getRequestDispatcher("/login").forward(request, response);
                                })
                                .defaultSuccessUrl("/home", true)
                )
                .rememberMe(httpSecurityRememberMeConfigurer ->
                        httpSecurityRememberMeConfigurer
                                .key("uniqueAndSecret")
                                .tokenValiditySeconds(60 * 60 * 24 * 7)
                                .userDetailsService(accountDetailsService))
                .logout(httpSecurityLogoutConfigurer ->
                        httpSecurityLogoutConfigurer
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .clearAuthentication(true)
                                .permitAll());

        return httpSecurity.build();
    }
}
