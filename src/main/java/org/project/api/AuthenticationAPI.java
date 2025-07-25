package org.project.api;

import jakarta.validation.Valid;
import org.project.model.dto.EmailDTO;
import org.project.model.dto.UserRegisterDTO;
import org.project.service.UserRegisterService;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationAPI {

    private UserService userService;
    private UserRegisterService userRegisterService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserRegisterService(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }
//    @PostMapping("/api/login/google")
//    public String login(String token) {
//
//    }

    @PostMapping("/api/password/reset")
    public void resetPassword(@RequestBody @Valid EmailDTO emailDTO) {
        userService.resetPassword(emailDTO.getEmail());
    }

    @PostMapping("/api/register")
    public void register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        userRegisterService.register(userRegisterDTO);
    }
}
