package org.project.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.project.dto.response.Response;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.enums.FamilyRelationship;
import org.project.enums.Gender;
import org.project.enums.PatientStatus;
import org.project.enums.UserRole;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.project.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoogleAuthService {

    private final GoogleTokenVerifierService googleVerifier;
    private final UserRepository userRepo;
    private final JWTUtils jwtUtils;

    @Autowired
    public GoogleAuthService(GoogleTokenVerifierService googleVerifier, UserRepository userRepo, JWTUtils jwtUtils) {
        this.googleVerifier = googleVerifier;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    private PatientRepository patientRepo;

    public ResponseEntity<?> loginWithGoogle(Map<String, String> body, HttpServletResponse response) {
        String token = body.get("token");

        try {
            Payload payload = googleVerifier.verify(token);
            if (payload == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid Google token"));
            }

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            Boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());


            UserEntity user = userRepo.findByEmail(email).orElseGet(() -> {
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setPasswordHash(UUID.randomUUID().toString());
                newUser.setUserRole(UserRole.PATIENT);
                newUser.setIsVerified(emailVerified);
                return userRepo.save(newUser);
            });

            boolean hasPatient = user.getPatientEntities() != null && !user.getPatientEntities().isEmpty();
            if (!hasPatient) {
                PatientEntity patient = new PatientEntity();
                patient.setUserEntity(user);
                patient.setFullName(name);
                patient.setAvatarUrl(picture);
                patient.setEmail(email);
                patient.setPhoneNumber("");
                patient.setBirthdate(new java.sql.Date(System.currentTimeMillis()));
                patient.setPatientStatus(PatientStatus.ACTIVE);
                patient.setFamilyRelationship(FamilyRelationship.SELF);
                patient.setGender(Gender.OTHER);
                patientRepo.save(patient);
            }

            // Sinh JWT
            String jwt = jwtUtils.generateToken(user);

            Cookie cookie = new Cookie("token", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of("message", "Google login success"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }
}
