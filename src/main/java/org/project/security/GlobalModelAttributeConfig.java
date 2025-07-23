package org.project.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.utils.JWTUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributeConfig {

    private final JWTUtils jwtUtils;

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        boolean isLoggedIn = token != null && jwtUtils.isValidToken(token);
        model.addAttribute("isLoggedIn", isLoggedIn);
        if (isLoggedIn) {
            Long userId = jwtUtils.extractUserId(token);
            model.addAttribute("loggedInUserId", userId);
        }
    }
}
