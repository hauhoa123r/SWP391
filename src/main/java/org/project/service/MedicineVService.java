package org.project.service;

import org.project.model.response.MedicineListVResponse;

import java.util.List;

public interface MedicineVService {
    List<MedicineListVResponse> getMedicineList();
}
