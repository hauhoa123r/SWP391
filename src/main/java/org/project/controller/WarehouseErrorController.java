package org.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling errors
 */
@Slf4j
@Controller
public class WarehouseErrorController implements ErrorController {

    /**
     * Handle error requests
     * @param request HTTP request
     * @param model Spring MVC Model
     * @return View name for error page
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        String errorMsg = (String) request.getAttribute("javax.servlet.error.message");
        String path = (String) request.getAttribute("javax.servlet.error.request_uri");
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // Only log resource not found errors at debug level to avoid filling logs
                if (path != null && (path.contains("/assets/") || path.contains("/frontend/"))) {
                    log.debug("Resource not found: {}", path);
                } else {
                    log.error("Error with status code {} occurred: {} for URI: {}", statusCode, errorMsg, path);
                }
                
                model.addAttribute("errorMessage", "Không tìm thấy trang này");
                return "error/404";
            } else {
                log.error("Error with status code {} occurred: {} for URI: {}", statusCode, errorMsg, path);
            }
        }
        
        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý yêu cầu của bạn");
        return "error/general";
    }
} 