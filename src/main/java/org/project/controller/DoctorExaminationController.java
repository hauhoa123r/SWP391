package org.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DoctorExaminationController {

    @GetMapping("/doctor/homepage")
    public ModelAndView doctorHomepage() {
        ModelAndView mv = new ModelAndView("/dashboard-doctor/index");
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

}
