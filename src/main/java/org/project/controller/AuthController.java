package org.project.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.project.dto.RegisterDTO;
import org.project.dto.response.Response;
import org.project.service.GoogleAuthService;
import org.project.service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserSecurityService userService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody RegisterDTO registerDTO) {
        Response response = userService.register(registerDTO);

        int status = response.getStatusCode();

        if (status < 100 || status > 599) {
            status = 500;
        }

        return ResponseEntity.status(status).body(response);
    }



    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body,
                                             HttpServletResponse response) {
        return googleAuthService.loginWithGoogle(body, response);
    }
}
