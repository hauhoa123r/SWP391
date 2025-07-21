package org.project.controller;

import org.project.service.DoctorService;
import org.project.service.HospitalService;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HospitalController {
    private HospitalService hospitalService;
    private ServiceService serviceService;
    private DoctorService doctorService;

    @Autowired
    public void setHospitalService(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @Autowired
    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/hospital")
    public String getAll() {
        return "/frontend/hospital";
    }

    @GetMapping("/hospital/detail/{hospitalId}")
    public String getHospitalDetail(@PathVariable Long hospitalId, ModelMap modelMap) {
        modelMap.put("hospital", hospitalService.getHospital(hospitalId));
        modelMap.put("services", serviceService.getTop3ServicesByHospital(hospitalId));
        modelMap.put("doctors", doctorService.getTop6DoctorsByHospital(hospitalId));
        return "/frontend/hospital-detail";
    }
}
