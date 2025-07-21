package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.SampleEntity;
import org.project.model.dto.ApproveResultDTO;
import org.project.utils.NumberUtils;
import org.project.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSampleRepositoryCustomImpl implements ResultSampleRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    private String joinSearchTestRequest(ApproveResultDTO approveResultDTO){
        StringBuilder results = new StringBuilder();
        results.append("join test_requests tr on sp.test_request_id = tr.test_request_id\n");
        results.append("join results r on r.sample_id = sp.sample_id\n");
        if(StringUtils.check(approveResultDTO.getPatientName())){
            results.append("join patients p on tr.patient_id = p.patient_id\n");
        }
        if(StringUtils.check(approveResultDTO.getTestType())){
            results.append("join test_types tt on tr.test_type_id = tt.test_type_id\n");
        }
        if(StringUtils.check(approveResultDTO.getTesterName())){
            results.append("join users u on u.user_id = r.approved_by\n");
            results.append("join staffs s on s.staff_id = u.user_id\n");
        }
        return results.toString();
    }

    private String whereSearchTestRequest(ApproveResultDTO approveResultDTO, Map<String, Object> params) throws IllegalAccessException {
        StringBuilder results = new StringBuilder("WHERE 1 = 1\n");
        Field[] fields = approveResultDTO.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(approveResultDTO);
            if (value != null
                    && !fieldName.equals("page")
                    && !fieldName.equals("size")
                    && !fieldName.equals("patientName")
                    && !fieldName.equals("testType")
                    && !fieldName.equals("status")
                    && !fieldName.equals("testerName")) {
                if (StringUtils.check(value.toString())) {
                    results.append("AND ").append(fieldName).append(" LIKE :").append(fieldName).append("\n");
                    params.put(fieldName, "%" + value + "%");
                } else if (NumberUtils.isLong(value.toString())) {
                    results.append("AND ").append(fieldName).append(" = :").append(fieldName).append("\n");
                    params.put(fieldName, value);
                }
            }
        }
        if (StringUtils.check(approveResultDTO.getPatientName())) {
            results.append("AND p.full_name LIKE :patientName\n");
            params.put("patientName", "%" + approveResultDTO.getPatientName() + "%");
        }
        if (StringUtils.check(approveResultDTO.getTestType())) {
            results.append("AND tt.test_type_name LIKE :testType\n");
            params.put("testType", "%" + approveResultDTO.getTestType() + "%");
        }
        if(StringUtils.check(approveResultDTO.getTesterName())){
            results.append("AND s.full_name LIKE :testerName\n");
            params.put("testerName", "%" + approveResultDTO.getTesterName() + "%");
        }
        results.append("AND r.result_entry_status LIKE :resultStatus\n");
        params.put("resultStatus", "%" + approveResultDTO.getStatus() + "%");
        return results.toString();
    }

    @Override
    public Page<SampleEntity> filterSampleEntityCustom(ApproveResultDTO approveResultDTO) throws IllegalAccessException {
        Map<String, Object> params = new HashMap<>();
        String baseQuery = "FROM samples sp" + "\n" + joinSearchTestRequest(approveResultDTO) + whereSearchTestRequest(approveResultDTO, params);
        String selectQuery = "SELECT sp.* " + baseQuery;
        String countQuery = "SELECT COUNT(*) " + baseQuery;

        int page = approveResultDTO.getPage();
        int size = approveResultDTO.getSize();

        Pageable pageRequest = PageRequest.of(page, size);

        Query dataQuery = entityManager.createNativeQuery(selectQuery, SampleEntity.class);
        Query countQueryObj = entityManager.createNativeQuery(countQuery);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            dataQuery.setParameter(entry.getKey(), entry.getValue());
            countQueryObj.setParameter(entry.getKey(), entry.getValue());
        }

        dataQuery.setFirstResult(page * size);
        dataQuery.setMaxResults(size);

        List<SampleEntity> resultList = dataQuery.getResultList();
        long total = ((Number) countQueryObj.getSingleResult()).longValue();

        return new PageImpl<>(resultList, pageRequest, total);
    }
}
