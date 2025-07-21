package org.project.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.HospitalRepository;
import org.project.repository.StaffRepository;
import org.project.service.DoctorStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

/**
 * Unit tests for the staff creation flow in {@link DoctorStaffController}.
 *
 * We use @WebMvcTest to load only the web layer (controller, filters, etc.) and
 * mock the remaining dependencies. {@link MockMvc} is used to simulate HTTP
 * requests to the controller without starting a real server.
 */
@WebMvcTest(controllers = DoctorStaffController.class)
class DoctorStaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorStaffService doctorStaffService;

    @MockBean
    private HospitalRepository hospitalRepository;
    @MockBean
    private DepartmentRepository departmentRepository;
    @MockBean
    private StaffRepository staffRepository;

    /*
     * ===== Helper =====
     */
    private DoctorStaffResponse mockSavedResponse() {
        DoctorStaffResponse res = new DoctorStaffResponse();
        res.setStaffId(1L);
        res.setFullName("Nguyen Van A");
        res.setStaffRole("DOCTOR");
        res.setStaffType("FULL_TIME");
        res.setRankLevel(5);
        return res;
    }

    @Test
    @DisplayName("GET /admin/doctor-staffs/create should render the create form")
    void showCreateForm_shouldReturnStatusOkAndView() throws Exception {
        // Mock repositories to avoid NPE when controller fetches data for dropdowns
        Mockito.when(hospitalRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/doctor-staffs/create"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/user-add"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staff"))
                .andExpect(MockMvcResultMatchers.model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("POST /admin/doctor-staffs/create with valid data should redirect to detail page")
    void createDoctorStaff_validRequest_shouldRedirect() throws Exception {
        // Arrange
        Mockito.when(doctorStaffService.createDoctorStaff(any(DoctorStaffRequest.class)))
                .thenReturn(mockSavedResponse());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/doctor-staffs/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "abc@gmail.com")
                        .param("phoneNumber", "0123456789")
                        .param("fullName", "Nguyen Van A")
                        .param("staffRole", "DOCTOR")
                        .param("staffType", "FULL_TIME")
                        .param("rankLevel", "5")
                        .param("hospitalId", "1")
                        .param("departmentId", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/doctor-staffs/detail/1"));

        // Verify that service was called once
        Mockito.verify(doctorStaffService).createDoctorStaff(any(DoctorStaffRequest.class));
    }

    @Test
    @DisplayName("POST /admin/doctor-staffs/create with invalid data should return form with errors")
    void createDoctorStaff_invalidRequest_shouldReturnErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/doctor-staffs/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        // Missing fullName (required) & phoneNumber invalid length
                        .param("email", "abc@gmail.com")
                        .param("phoneNumber", "123")
                        .param("staffRole", "DOCTOR")
                        .param("staffType", "FULL_TIME")
                        .param("rankLevel", "0")
                        .param("hospitalId", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attributeExists("org.springframework.validation.BindingResult.staff"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/doctor-staffs/create"));
    }
}
