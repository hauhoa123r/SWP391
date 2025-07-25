package org.project.service;


import org.project.model.response.MedicineListVResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductVService{
    Page<MedicineListVResponse> getMedicineList(String keyword, Pageable pageable);
}
