package org.project.controller;

import org.project.converter.DoctorHomePageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DoctorExaminationController {

    @Autowired
    private DoctorHomePageConverter doctorHomePageConverter;

    @GetMapping("/doctor/homepage")
    public ModelAndView doctorHomepage() {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/index");
        mv.addObject("doctorHomePage", doctorHomePageConverter.doctorHomepageResponse(60L));
        return mv;
    }

    @GetMapping("/doctor/examination")
    public ModelAndView doctorExamination() {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/examination");
        return mv;
    }

    @GetMapping("/doctor/schedule")
    public ModelAndView doctorSchedule() {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/schedule");
        return mv;
    }
    @GetMapping("/doctor/appointment")
    public ModelAndView doctorAppointment() {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/appointment");
        return mv;
    }
    @GetMapping("/doctor/result")
    public ModelAndView doctorReview() {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/result");
        return mv;
    }
}
