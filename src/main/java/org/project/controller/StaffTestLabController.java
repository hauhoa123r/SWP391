package org.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.project.converter.AssignmentListConverter;
import org.project.converter.SetDateConverter;
import org.project.converter.TestItemConverter;
import org.project.converter.ViewTestRequestDetailConverter;
import org.project.entity.StaffEntity;
import org.project.enums.StaffRole;
import org.project.model.dto.AssignmentListDTO;
import org.project.model.response.SetDateGetSampleResponse;
import org.project.model.response.SetResultResponse;
import org.project.model.response.ViewResultResponse;
import org.project.repository.AppointmentRepository;
import org.project.repository.AssignmentRepository;
import org.project.repository.PatientRepository;
import org.project.repository.StaffRepository;
import org.project.service.SampleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("lab")
public class StaffTestLabController {

    private  TestItemConverter testItemConverter;
    private  StaffRepository staffRepository;
    private  PatientRepository patientRepository;
    private  AppointmentRepository appointmentRepository;
    private  AssignmentListConverter assignmentListConverter;
    private  AssignmentRepository assignmentRepository;
    private  SampleScheduleService sampleScheduleService;
    private  SetDateConverter setDateConverter;
    private ViewTestRequestDetailConverter viewTestRequestDetailConverter;
    @Autowired
    public void setTestItemConverter(TestItemConverter testItemConverter) {
        this.testItemConverter = testItemConverter;
    }
    @Autowired
    public void setViewTestRequestDetailConverter(ViewTestRequestDetailConverter viewTestRequestDetailConverter) {
        this.viewTestRequestDetailConverter = viewTestRequestDetailConverter;
    }

    @Autowired
    public void setStaffRepository(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Autowired
    public void setPatientRepository(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Autowired
    public void setAppointmentRepository(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Autowired
    public void setAssignmentListConverter(AssignmentListConverter assignmentListConverter) {
        this.assignmentListConverter = assignmentListConverter;
    }

    @Autowired
    public void setAssignmentRepository(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Autowired
    public void setSampleScheduleService(SampleScheduleService sampleScheduleService) {
        this.sampleScheduleService = sampleScheduleService;
    }

    @Autowired
    public void setSetDateConverter(SetDateConverter setDateConverter) {
        this.setDateConverter = setDateConverter;
    }

    @GetMapping("/homepage")
    public ModelAndView homePageView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/index");
        StaffEntity staffEntity = staffRepository.findByStaffRoleAndId(StaffRole.DOCTOR, 602L);
        modelAndView.addObject("staffs", staffEntity);
        modelAndView.addObject("totalPatient", patientRepository.countAllPatients());
        modelAndView.addObject("currentURI", request.getRequestURI());
        modelAndView.addObject("totalToday", appointmentRepository.countTotalAppointmentsToday());
        return modelAndView;
    }

        @GetMapping("/tests")
        public String getAssignments(
                @ModelAttribute() AssignmentListDTO assignmentListDTO,
                                     Model model,
                                     HttpServletRequest request) {
            Pageable pageable = PageRequest.of(assignmentListDTO.getPage(), assignmentListDTO.getSize());
            Page<AssignmentListDTO> assignments = assignmentListConverter.getAllAssignmentListPageable(pageable);
            model.addAttribute("assignments", assignments);
            model.addAttribute("currentURI", request.getRequestURI());
            return "dashboard-staff-test/test-list";
        }

    @GetMapping("/receive-items")
    public ModelAndView receiveListView(
            @RequestParam(defaultValue = "0", name = "page") int pageIndex,
            HttpServletRequest request,
            Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/receive-list");
        Pageable pageable = PageRequest.of(pageIndex, 10);
        Page<AssignmentListDTO> assignments = assignmentListConverter.getAllRequestPending(pageable);
        modelAndView.addObject("assignments", assignments);
        modelAndView.addObject("currentURI", request.getRequestURI());
        model.addAttribute("currentPage", assignments.getNumber());
        model.addAttribute("totalPages", assignments.getTotalPages());
        model.addAttribute("totalItems", assignments.getTotalElements());
        return modelAndView;
    }

    @GetMapping("/sampling-schedule")
    public ModelAndView samplingScheduleView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/sampling-schedule");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/sample-confirmation")
    public ModelAndView sampleConfirmationView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/sample-confirmation");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/set-result")
    public ModelAndView setResultView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/set-result");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/confirm-result")
    public ModelAndView confirmResultView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/confirm-result");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/send-result")
    public ModelAndView sendResultView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/send-result");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/manage-sample")
    public ModelAndView manageSampleView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/manage-sample");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/helpsupport")
    public ModelAndView helpSupportView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/helpsupport");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/setting")
    public ModelAndView settingView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/setting");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/result/{id}")
    public ModelAndView resultView(HttpServletRequest request, Model model, @PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/result-test-details");
        SetResultResponse result = testItemConverter.toConverterSetResultResponse(id);
        modelAndView.addObject("result", result);
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/set-date")
    public ModelAndView setDateView(HttpServletRequest request, Model model, @RequestParam(required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/set-date-time");
        SetDateGetSampleResponse setDateGetSampleResponse = setDateConverter.toConverterSetDateGetSampleResponse(id);
        setDateGetSampleResponse.setTestRequestId(id);
        modelAndView.addObject("patient", setDateGetSampleResponse);
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }
    @GetMapping("/test-detail/{id}")
    public ModelAndView viewTestDetail(HttpServletRequest request, Model model,  @PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/lab-test-details");
        SetResultResponse result = testItemConverter.toConverterSetResultResponse(id);
        modelAndView.addObject("result", result);
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }
    @GetMapping("/view-test-request/{id}")
    public ModelAndView viewResult(HttpServletRequest request, Model model, @PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/view-result");
        ViewResultResponse viewResultResponse = viewTestRequestDetailConverter.viewTestRequestDetailConverter(id);
        modelAndView.addObject("currentURI", request.getRequestURI());
        modelAndView.addObject("result", viewResultResponse);
        return modelAndView;
    }
    @GetMapping("/view-result-patient/{id}")
    public ModelAndView viewResultPatient(HttpServletRequest request, Model model, @PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/view-result-patient");
        ViewResultResponse viewResultResponse = viewTestRequestDetailConverter.viewTestRequestDetailConverter(id);
        modelAndView.addObject("currentURI", request.getRequestURI());
        modelAndView.addObject("result", viewResultResponse);
        return modelAndView;
    }

    @GetMapping("/add/sample")
    public ModelAndView addSampleView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/add-sample");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }
}