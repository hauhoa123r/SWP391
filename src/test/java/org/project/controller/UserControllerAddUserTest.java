package org.project.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.project.entity.UserEntity;
import org.project.enums.StaffRole;
import org.project.enums.UserRole;
import org.project.repository.PatientRepository;
import org.project.repository.StaffRepository;
import org.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Add-User flow via {@code UserController}.
 *
 * NOTE: All tests are transactional, therefore DB changes are rolled back automatically.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerAddUserTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    StaffRepository staffRepository;

    private Map<String, String> defaultUserParams;

    @BeforeEach
    void setUp() {
        defaultUserParams = new HashMap<>();
        defaultUserParams.put("email", "john.doe@example.com");
        defaultUserParams.put("phone_number", "0123456789");
        defaultUserParams.put("password", "secret123");
        defaultUserParams.put("two_factor_enabled", "on");
    }

    /* ----------------- Helper ----------------- */
    private org.springframework.test.web.servlet.ResultActions doPost(Map<String, String> params) throws Exception {
        var builder = post("/users/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        params.forEach(builder::param);
        return mockMvc.perform(builder);
    }

    /* ---------------- Negative validations (400) ---------------- */
    @Test
    @DisplayName("Should fail when email is empty")
    void createUser_emptyEmail() throws Exception {
        var params = new HashMap<>(defaultUserParams);
        params.remove("email");
        params.put("role", "ADMIN");

        doPost(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail on invalid email format")
    void createUser_invalidEmail() throws Exception {
        var params = new HashMap<>(defaultUserParams);
        params.put("email", "invalid_format");
        params.put("role", "ADMIN");

        doPost(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail when phone number is empty")
    void createUser_emptyPhone() throws Exception {
        var params = new HashMap<>(defaultUserParams);
        params.remove("phone_number");
        params.put("role", "ADMIN");
        doPost(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail when password < 6 chars")
    void createUser_shortPassword() throws Exception {
        var params = new HashMap<>(defaultUserParams);
        params.put("password", "123");
        params.put("role", "ADMIN");
        doPost(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail when role is PATIENT but missing fullname/birthdate")
    void createPatient_missingRequired() throws Exception {
        var params = new HashMap<>(defaultUserParams);
        params.put("role", "PATIENT");
        // intentionally NOT putting full_name / birthdate
        doPost(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail when role STAFF but missing hospital_id or staff_role")
    void createStaff_missingRequired() throws Exception {
        var params = new HashMap<>(defaultUserParams);
        params.put("role", "STAFF");
        params.put("full_name", "Alice Staff");
        params.put("department_id", "1");
        // missing staff_role and hospital_id
        doPost(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail when email already exists")
    void createUser_duplicateEmail() throws Exception {
        // seed existing
        UserEntity already = new UserEntity();
        already.setId(99L);
        already.setEmail(defaultUserParams.get("email"));
        already.setPasswordHash("pwd");
        already.setUserRole(UserRole.ADMIN);
        userRepository.save(already);

        var params = new HashMap<>(defaultUserParams);
        params.put("role", "ADMIN");
        doPost(params)
                .andExpect(status().isConflict()); // expecting 409 Duplicate
    }

    @Test
    @DisplayName("Should fail when role not selected")
    void createUser_roleMissing() throws Exception {
        var params = new HashMap<>(defaultUserParams);
        doPost(params)
                .andExpect(status().isBadRequest());
    }

    /* ---------------- Positive flows ---------------- */

    @Test
    @Rollback
    @DisplayName("Create PATIENT with full data – should insert into users & patients")
    void createPatient_success() throws Exception {
        long userCountBefore = userRepository.count();
        long patientCountBefore = patientRepository.count();

        var params = new HashMap<>(defaultUserParams);
        params.put("role", "PATIENT");
        params.put("full_name", "Patient One");
        params.put("gender", "MALE");
        params.put("birthdate", "1990-01-01");
        params.put("address", "Somewhere");
        params.put("relationship", "SELF");

        doPost(params).andExpect(status().is3xxRedirection());

        assertThat(userRepository.count()).isEqualTo(userCountBefore + 1);
        assertThat(patientRepository.count()).isEqualTo(patientCountBefore + 1);
    }

    @Test
    @Rollback
    @DisplayName("Create STAFF with full data – should insert into users & staffs")
    void createStaff_success() throws Exception {
        long userBefore = userRepository.count();
        long staffBefore = staffRepository.count();

        var params = new HashMap<>(defaultUserParams);
        params.put("role", "STAFF");
        params.put("full_name", "Doctor Strange");
        params.put("staff_role", StaffRole.DOCTOR.name());
        params.put("hospital_id", "1");
        params.put("department_id", "1");
        params.put("hire_date", "2020-01-01");

        doPost(params).andExpect(status().is3xxRedirection());

        assertThat(userRepository.count()).isEqualTo(userBefore + 1);
        assertThat(staffRepository.count()).isEqualTo(staffBefore + 1);
    }

    @Test
    @Rollback
    @DisplayName("Create ADMIN only – should insert into users table only")
    void createAdmin_success() throws Exception {
        long userBefore = userRepository.count();

        var params = new HashMap<>(defaultUserParams);
        params.put("role", "ADMIN");

        doPost(params).andExpect(status().is3xxRedirection());

        assertThat(userRepository.count()).isEqualTo(userBefore + 1);
    }

}
