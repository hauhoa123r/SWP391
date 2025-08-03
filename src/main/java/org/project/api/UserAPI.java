package org.project.api;

import jakarta.validation.Valid;
import org.project.model.dto.ChangeEmailDTO;
import org.project.model.dto.ChangePasswordDTO;
import org.project.model.dto.ChangePhoneNumberDTO;
import org.project.security.AccountDetails;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Validated
public class UserAPI {

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PatchMapping("/change-phone")
    public ResponseEntity<?> changePhoneNumber(@AuthenticationPrincipal AccountDetails accountDetails,
                                                @RequestBody ChangePhoneNumberDTO changePhoneNumberDTO,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(f -> f.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("error", errorMessage));
        }

        if (!passwordEncoder.matches(changePhoneNumberDTO.getPassword(), accountDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu cũ không đúng");
        }
        if (userService.isExistPhoneNumber(changePhoneNumberDTO.getPhoneNumber())){
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Số điện thoại đã được sử dụng");
        }

        userService.updatePhoneNumber(accountDetails.getUserEntity().getId(), changePhoneNumberDTO.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/change-email")
    public ResponseEntity<?> changeEmail(
            @AuthenticationPrincipal AccountDetails account,
            @Valid @RequestBody ChangeEmailDTO dto,
            BindingResult br) {

        if (br.hasErrors()) {
            String errs = br.getFieldErrors().stream()
                    .map(f -> f.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", errs));
        }

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu cũ không đúng");
        }

        if (userService.isExistEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email đã được sử dụng");
        }

        userService.updateEmail(account.getUserEntity().getId(), dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal AccountDetails account,
            @Valid @RequestBody ChangePasswordDTO dto,
            BindingResult br) {

        if (br.hasErrors()) {
            String errs = br.getFieldErrors().stream()
                    .map(f -> f.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", errs));
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), account.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Mật khẩu cũ không đúng");
        }

        if(!dto.getNewPassword().equals(dto.getConfirmPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Mật khẩu xác nhận không trùng với mật khẩu mới");
        }

        // 3. Thực hiện cập nhật mật khẩu
        userService.updatePassword(account.getUserEntity().getId(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
