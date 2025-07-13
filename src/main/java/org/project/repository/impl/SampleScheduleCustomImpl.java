package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.SampleEntity;
import org.project.model.dto.SampleFilterDTO;
import org.project.utils.NumberUtils;
import org.project.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SampleScheduleCustomImpl implements SampleScheduleCustom{

    @PersistenceContext
    EntityManager entityManager;

    private String joinSearchTestRequest(SampleFilterDTO sampleFilterDTO){
        StringBuilder results = new StringBuilder();
        results.append("join test_requests tr on sp.test_request_id = tr.test_request_id\n");
        if(StringUtils.check(sampleFilterDTO.getPatientName())){
            results.append("join patients p on tr.patient_id = p.patient_id\n");
        }
        if(StringUtils.check(sampleFilterDTO.getTestType())){
            results.append("join test_types tt on tr.test_type_id = tt.test_type_id\n");
        }
        return results.toString();
    }

    private String whereSearchTestRequest(SampleFilterDTO sampleFilterDTO, Map<String, Object> params) throws IllegalAccessException {
        StringBuilder results = new StringBuilder("WHERE 1 = 1\n");
        Field[] fields = sampleFilterDTO.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(sampleFilterDTO);
            if (value != null
                    && !fieldName.equals("page")
                    && !fieldName.equals("size")
                    && !fieldName.equals("patientName")
                    && !fieldName.equals("testType")) {
                if (StringUtils.check(value.toString())) {
                    results.append("AND ").append(fieldName).append(" LIKE :").append(fieldName).append("\n");
                    params.put(fieldName, "%" + value + "%");
                } else if (NumberUtils.isLong(value.toString())) {
                    results.append("AND ").append(fieldName).append(" = :").append(fieldName).append("\n");
                    params.put(fieldName, value);
                }
            }
        }
        if (StringUtils.check(sampleFilterDTO.getPatientName())) {
            results.append("AND p.full_name LIKE :patientName\n");
            params.put("patientName", "%" + sampleFilterDTO.getPatientName() + "%");
        }
        if (StringUtils.check(sampleFilterDTO.getTestType())) {
            results.append("AND tt.test_type_name LIKE :testType\n");
            params.put("testType", "%" + sampleFilterDTO.getTestType() + "%");
        }
        results.append("AND sp.sample_status LIKE :sampleStatus\n");
        params.put("sampleStatus", "%" + "pending" + "%");
        return results.toString();
    }

    @Override
    public Page<SampleEntity> filterSampleEntityCustom(SampleFilterDTO sampleFilterDTO) throws IllegalAccessException {
        Map<String, Object> params = new HashMap<>();
        String baseQuery = "FROM samples sp" + "\n" + joinSearchTestRequest(sampleFilterDTO) + whereSearchTestRequest(sampleFilterDTO, params);
        String selectQuery = "SELECT sp.* " + baseQuery;
        String countQuery = "SELECT COUNT(*) " + baseQuery;

        int page = sampleFilterDTO.getPage();
        int size = sampleFilterDTO.getSize();

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
