package org.project.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.project.dto.RegisterDTO;
import org.project.dto.response.Response;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.enums.PatientStatus;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.project.exception.OurException;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.project.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserSecutiryServiceImpl implements UserSecurityService {


    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    public Response register(RegisterDTO dto) {
        Response response = new Response();
        try {

            UserEntity user = new UserEntity();
            user.setEmail(dto.getEmail());
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setUserRole(UserRole.PATIENT);
            user.setIsVerified(true);
            user.setUserStatus(UserStatus.ACTIVE);
            UserEntity savedUser = userRepository.save(user);

            PatientEntity patient = new PatientEntity();
            patient.setUserEntity(savedUser);
            patient.setEmail(dto.getEmail());
            patient.setPhoneNumber(dto.getPhoneNumber());
            patient.setFullName(dto.getFullName());
            patient.setAddress(dto.getAddress());
            patient.setBirthdate(Date.valueOf(dto.getBirthdate()));
            patient.setPatientStatus(PatientStatus.ACTIVE);
            patient.setGender(dto.getGender());
            patient.setFamilyRelationship(dto.getFamilyRelationship());

            patientRepository.save(patient);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", savedUser.getId());
            userInfo.put("email", savedUser.getEmail());
            userInfo.put("phone", savedUser.getPhoneNumber());
            userInfo.put("fullname", patient.getFullName());
            userInfo.put("address", patient.getAddress());
            userInfo.put("birthdate", patient.getBirthdate());
            userInfo.put("role", savedUser.getUserRole());
            response.setData(userInfo);

            response.setStatusCode(201);
            response.setMessage("Register success");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error during registration: " + e.getMessage());
            return response;
        }

        return response;
    }


    public String login(String email, String password, String redirectTo, HttpServletResponse response) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        UserEntity user = userRepository.findByEmail(email).orElseThrow();
        if ("INACTIVE".equals(user.getUserStatus())) {
            throw new OurException("Tài khoản bị vô hiệu hoá");
        }

        String token = jwtUtils.generateToken(user);
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        if (redirectTo != null && !redirectTo.isBlank()) {
            return "redirect:" + redirectTo;
        }

        String redirectUrl;
        switch (user.getUserRole()) {
            case ADMIN:
                redirectUrl = "redirect:/admin";
                break;
            case PATIENT:
                redirectUrl = "redirect:/patient/showAddPrevious";
                break;
            case STAFF:
                if (user.getStaffEntity() != null && user.getStaffEntity().getStaffRole() != null) {
                    switch (user.getStaffEntity().getStaffRole()) {
                        case DOCTOR:
                            redirectUrl = "redirect:/doctor";
                        case PHARMACIST:
                            redirectUrl = "redirect:/staff/pharmacy";
                            break;
                        case TECHNICIAN:
                            redirectUrl = "redirect:/lab/homepage";
                            break;
                        case SCHEDULING_COORDINATOR:
                            redirectUrl = "redirect:/staff/schedule";
                            break;
                        case INVENTORY_MANAGER:
                            redirectUrl = "redirect:/staff/inventory";
                            break;

                        default:
                            throw new RuntimeException("Unknown staff role");
                    }
                } else {
                    throw new RuntimeException("Missing staff role for staff user");
                }
                break;
            default:
                throw new RuntimeException("Unknown user role");
        }

        return redirectUrl;
    }
}