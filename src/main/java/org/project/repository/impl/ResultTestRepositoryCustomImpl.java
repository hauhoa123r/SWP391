package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.AppointmentEntity;
import org.project.model.dto.ResultTestDTO;
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

public class ResultTestRepositoryCustomImpl implements ResultTestRepositoryCustom{


    @PersistenceContext
    EntityManager entityManager;

    private String joinSearchTestRequest(ResultTestDTO resultTestDTO){
        StringBuilder results = new StringBuilder();
        results.append("join test_requests t on t.appointment_id = a.appointment_id\n");
        if(StringUtils.check(resultTestDTO.getPatientName())){
            results.append("join patients p on a.patient_id = p.patient_id\n");
        }
        if(StringUtils.check(resultTestDTO.getTestType())){
            results.append("join test_types tt on tt.test_type_id = t.test_type_id\n");
        }
        if(StringUtils.check(resultTestDTO.getDoctorName())){
            results.append("join staffs s on s.staff_id = a.doctor_id\n");
        }
        return results.toString();
    }

    private String whereSearchTestRequest(ResultTestDTO resultTestDTO, Map<String, Object> params) throws IllegalAccessException {
        StringBuilder results = new StringBuilder("WHERE 1 = 1\n");
        Field[] fields = resultTestDTO.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(resultTestDTO);
            if (value != null
                    && !fieldName.equals("page")
                    && !fieldName.equals("size")
                    && !fieldName.equals("patientName")
                    && !fieldName.equals("testType")
                    && !fieldName.equals("status")
                    && !fieldName.equals("doctorName")){
                if (StringUtils.check(value.toString())) {
                    results.append("AND ").append(fieldName).append(" LIKE :").append(fieldName).append("\n");
                    params.put(fieldName, "%" + value + "%");
                } else if (NumberUtils.isLong(value.toString())) {
                    results.append("AND ").append(fieldName).append(" = :").append(fieldName).append("\n");
                    params.put(fieldName, value);
                }
            }
        }
        if(StringUtils.check(resultTestDTO.getDoctorName())){
            results.append("AND s.full_name LIKE :doctorName\n");
            params.put("doctorName", "%" + resultTestDTO.getDoctorName() + "%");
        }
        if (StringUtils.check(resultTestDTO.getPatientName())) {
            results.append("AND p.full_name LIKE :patientName\n");
            params.put("patientName", "%" + resultTestDTO.getPatientName() + "%");
        }
        if (StringUtils.check(resultTestDTO.getTestType())) {
            results.append("AND tt.test_type_name LIKE :testType\n");
            params.put("testType", "%" + resultTestDTO.getTestType() + "%");
        }
        results.append("And a.result_url is null\n");
        results.append("AND a.appointment_id NOT IN (SELECT appointment_id FROM test_requests WHERE request_status IN ('pending', 'completed', 'received'))\n");
        return results.toString();
    }

    @Override
    public Page<AppointmentEntity> filterAppointmentEntityCustom(ResultTestDTO resultTestDTO) throws IllegalAccessException {
        Map<String, Object> params = new HashMap<>();
        String baseQuery = "FROM appointments a" + "\n" + joinSearchTestRequest(resultTestDTO) + whereSearchTestRequest(resultTestDTO, params);
        String selectQuery = "SELECT distinct  a.* " + baseQuery;
        String countQuery = "SELECT COUNT(DISTINCT a.appointment_id) " + baseQuery;
        int page = resultTestDTO.getPage();
        int size = resultTestDTO.getSize();

        Pageable pageRequest = PageRequest.of(page, size);
        Query dataQuery = entityManager.createNativeQuery(selectQuery, AppointmentEntity.class);
        Query countQueryObj = entityManager.createNativeQuery(countQuery);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            dataQuery.setParameter(entry.getKey(), entry.getValue());
            countQueryObj.setParameter(entry.getKey(), entry.getValue());
        }
        dataQuery.setFirstResult(page * size);
        dataQuery.setMaxResults(size);
        List<AppointmentEntity> resultList = dataQuery.getResultList();
        long total = ((Number) countQueryObj.getSingleResult()).longValue();

        return new PageImpl<>(resultList, pageRequest, total);
    }
}
