package org.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.project.converter.AssignmentListConverter;
import org.project.entity.StaffEntity;
import org.project.enums.StaffRole;
import org.project.model.dto.AssignmentListDTO;
import org.project.repository.AppointmentRepository;
import org.project.repository.PatientRepository;
import org.project.repository.StaffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("lab")
public class StaffTestLabController {

    private final StaffRepository staffRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AssignmentListConverter assignmentListConverter;
    public StaffTestLabController(StaffRepository staffRepository,
                                  PatientRepository patientRepository,
                                  AppointmentRepository appointmentRepository,
                                  AssignmentListConverter assignmentListConverter) {
        this.staffRepository = staffRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.assignmentListConverter = assignmentListConverter;
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

    @GetMapping("/test-list")
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

    @GetMapping("/receive-list")
    public ModelAndView receiveListView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/receive-list");
        modelAndView.addObject("currentURI", request.getRequestURI());
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

    @GetMapping("/lab-statistics")
    public ModelAndView labStatisticsView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/lab-statistics");
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

    @GetMapping("/result")
    public ModelAndView resultView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/result-test-details");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }

    @GetMapping("/set-date")
    public ModelAndView setDateView(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/set-date-time");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }
    @GetMapping("/test-detail")
    public ModelAndView viewTestDetail(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView("dashboard-staff-test/lab-test-details");
        modelAndView.addObject("currentURI", request.getRequestURI());
        return modelAndView;
    }
}