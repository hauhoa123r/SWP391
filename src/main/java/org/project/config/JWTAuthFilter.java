//package org.project.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.project.service.CustomUserDetailsService;
//import org.project.utils.JWTUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JWTAuthFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JWTUtils jwtUtils;
//    @Autowired
//    private CustomUserDetailsService customUserDetailsService;
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String jwtToken = null;
//        String userEmail = null;
//
//        final String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            jwtToken = authHeader.substring(7);
//        } else if (request.getCookies() != null) {
//            for (var cookie : request.getCookies()) {
//                if ("token".equals(cookie.getName())) {
//                    jwtToken = cookie.getValue();
//                    break;
//                }
//            }
//        }
//        if (jwtToken == null) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            userEmail = jwtUtils.extractUsername(jwtToken);
//            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
//                if (jwtUtils.isValidToken(jwtToken, userDetails)) {
//                    UsernamePasswordAuthenticationToken token =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(token);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("JWT validation failed: " + e.getMessage());
//
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
