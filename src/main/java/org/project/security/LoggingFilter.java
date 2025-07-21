package org.project.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@Order(1)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger requestLogger = LoggerFactory.getLogger("REQUEST_LOG");
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOG");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();


        if (uri.startsWith("/assets/") || uri.startsWith("/static/") ||
                uri.matches("(?i).*(\\.css|\\.js|\\.png|\\.jpg|\\.jpeg|\\.gif|\\.ico|\\.woff2)$") ||
                uri.equals("/favicon.ico")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            errorLogger.error("❌ Lỗi xử lý request [{} {}]: {}", request.getMethod(), uri, ex.getMessage(), ex);
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logUserAction(wrappedRequest, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logUserAction(ContentCachingRequestWrapper request,
                               ContentCachingResponseWrapper response,
                               long durationMillis) {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String user = getCurrentUser();
        String roles = getCurrentUserRoles();
        int status = response.getStatus();

        String requestBody = getRequestBody(request);
        if (requestBody.length() > 500) requestBody = requestBody.substring(0, 500) + "...";

        String requestQuery = "";
        if ("GET".equalsIgnoreCase(method)) {
            String rawQuery = request.getQueryString();
            requestQuery = (rawQuery != null) ? java.net.URLDecoder.decode(rawQuery, StandardCharsets.UTF_8) : "";
        }

        String inputData;
        if ("GET".equalsIgnoreCase(method)) {
            inputData = (requestQuery != null && !requestQuery.isBlank()) ? requestQuery : "[Không có]";
        } else {
            inputData = requestBody.isBlank() ? "[Không có]" : requestBody;
        }

        String responseBody = "";
        String contentType = response.getContentType();

        if (contentType != null && contentType.contains("application/json")) {
            responseBody = getResponseBody(response);
            if (responseBody.length() > 500) responseBody = responseBody.substring(0, 500) + "...";
        } else {
            responseBody = "[BỎ QUA - PHẢN HỒI HTML]";
        }

        requestLogger.info("""
            📌 [{}] {} {} từ IP: {}
            👤 Người dùng: {} (vai trò: {})
            📥 Nội dung gửi: {}
            📤 Trạng thái HTTP: {}, Thời gian xử lý: {} ms
            📦 Phản hồi trả về: {}
            """,
                LocalDateTime.now(), method, uri, ip,
                user, roles,
                inputData,
                status, durationMillis,
                responseBody
        );
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            return maskSensitiveData(body);
        } catch (Exception e) {
            return "[KHÔNG ĐỌC ĐƯỢC NỘI DUNG GỬI]";
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            return maskSensitiveData(body);
        } catch (Exception e) {
            return "[KHÔNG ĐỌC ĐƯỢC NỘI DUNG PHẢN HỒI]";
        }
    }

    private String maskSensitiveData(String input) {
        return input
                .replaceAll("(?i)(\"password\"\\s*:\\s*\")(.*?)(\")", "$1*****$3")
                .replaceAll("(?i)(password=)([^&]*)", "$1*****")
                .replaceAll("(?i)(\"token\"\\s*:\\s*\")(.*?)(\")", "$1[HIDDEN]$3")
                .replaceAll("(?i)(token=)([^&]*)", "$1[HIDDEN]")
                .replaceAll("(?i)(\"ssn\"\\s*:\\s*\")(.*?)(\")", "$1[HIDDEN]$3")
                .replaceAll("(?i)(email=)([^&]*)", "$1[HIDDEN]");
    }

    private String getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();

                if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
                    String email = oauth2User.getAttribute("email");
                    return email != null ? email : "[Không có email từ OAuth2]";
                }
                return authentication.getName();
            }
        } catch (Exception e) {
            return "người dùng chưa đăng nhập";
        }
        return "người dùng chưa đăng nhập";
    }

    private String getCurrentUserRoles() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "Không xác định";
            }
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "Không xác định";
        }
    }
}
