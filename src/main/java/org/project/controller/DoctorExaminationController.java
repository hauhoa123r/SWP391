package org.project.controller;

import org.project.converter.DoctorExaminationConverter;
import org.project.converter.DoctorHomePageConverter;
import org.project.model.response.DoctorExaminationResponse;
import org.project.security.AccountDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DoctorExaminationController {

    @Autowired
    private DoctorHomePageConverter doctorHomePageConverter;

    @Autowired
    private DoctorExaminationConverter doctorExaminationConverter;

    @GetMapping("/doctor/homepage")
    public ModelAndView doctorHomepage(@AuthenticationPrincipal AccountDetails accountDetails) {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/index");
        mv.addObject("doctorHomePage", doctorHomePageConverter.doctorHomepageResponse(accountDetails.getUserEntity().getId()));
        return mv;
    }

    @GetMapping("/doctor/examination/{id}")
    public ModelAndView doctorExamination(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/examination");
        DoctorExaminationResponse doctorExaminationResponse = doctorExaminationConverter.getInformationPatientToAppointment(id);
        mv.addObject("information", doctorExaminationResponse);
        return mv;
    }

    @GetMapping("/doctor/schedule")
    public ModelAndView doctorSchedule() {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/schedule");
        return mv;
    }

    @GetMapping("/doctor/appointment")
    public ModelAndView doctorAppointment(@AuthenticationPrincipal AccountDetails accountDetails) {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/appointment");
        mv.addObject("doctorId", accountDetails.getUserEntity().getStaffEntity().getId());
        return mv;
    }

    @GetMapping("/doctor/result")
    public ModelAndView doctorReview(@AuthenticationPrincipal AccountDetails accountDetails) {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/result");
        mv.addObject("doctorId", accountDetails.getUserEntity().getStaffEntity().getId());
        return mv;
    }

    @GetMapping("/doctor/appointment/{id}")
    public ModelAndView doctorAppointment(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/appointment-detail");
        DoctorExaminationResponse doctorExaminationResponse = doctorExaminationConverter.getInformationPatientToAppointment(id);
        mv.addObject("information", doctorExaminationResponse);
        return mv;
    }
    @GetMapping("/doctor/test/request/{id}")
    public ModelAndView doctorTestRequest(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/add-test-request");
        DoctorExaminationResponse doctorExaminationResponse = doctorExaminationConverter.getInformationPatientToAppointment(id);
        mv.addObject("information", doctorExaminationResponse);
        return mv;
    }
    @GetMapping("/doctor/prescription/{id}")
    public ModelAndView doctorPrescription(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/prescription");
        DoctorExaminationResponse doctorExaminationResponse = doctorExaminationConverter.getInformationPatientToAppointment(id);
        mv.addObject("information", doctorExaminationResponse);
        return mv;
    }
}
