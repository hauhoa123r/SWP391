package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.TestRequestEntity;
import org.project.enums.RequestStatus;
import org.project.model.dto.AssignmentListDTO;
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
public class ReceiveRepositoryCustomImpl implements ReceiveRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    private String joinSearchTestRequest(AssignmentListDTO assignmentListDTO){
        StringBuilder results = new StringBuilder();
        if(StringUtils.check(assignmentListDTO.getPatientName())){
            results.append("JOIN patients p ON t.patient_id = p.patient_id\n");
        }
        if(StringUtils.check(assignmentListDTO.getDepartmentName()) || StringUtils.check(assignmentListDTO.getDoctorName())){
            results.append("Join staffs s ON t.doctor_id = s.staff_id\n");
            results.append("JOIN departments d ON s.department_id = d.department_id\n");
        }
        if(StringUtils.check(assignmentListDTO.getTestType())){
            results.append("JOIN test_types tt ON t.test_type_id = tt.test_type_id\n");
        }
        return results.toString();
    }

    private String whereSearchTestRequest(AssignmentListDTO assignmentListDTO, Map<String, Object> params) throws IllegalAccessException {
        StringBuilder results = new StringBuilder("WHERE 1 = 1\n");
        Field[] fields = assignmentListDTO.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(assignmentListDTO);
            if (value != null
                    && !fieldName.equals("page")
                    && !fieldName.equals("size")
                    && !fieldName.equals("patientName")
                    && !fieldName.equals("departmentName")
                    && !fieldName.equals("testType")
                    && !fieldName.equals("doctorName")
                    && !fieldName.equals("status")) {
                if (StringUtils.check(value.toString())) {
                    results.append("AND ").append(fieldName).append(" LIKE :").append(fieldName).append("\n");
                    params.put(fieldName, "%" + value + "%");
                } else if (NumberUtils.isLong(value.toString())) {
                    results.append("AND ").append(fieldName).append(" = :").append(fieldName).append("\n");
                    params.put(fieldName, value);
                }
            }
        }
        if (StringUtils.check(assignmentListDTO.getPatientName())) {
            results.append("AND p.full_name LIKE :patientName\n");
            params.put("patientName", "%" + assignmentListDTO.getPatientName() + "%");
        }
        if (StringUtils.check(assignmentListDTO.getDepartmentName())) {
            results.append("AND d.name LIKE :departmentName\n");
            params.put("departmentName", "%" + assignmentListDTO.getDepartmentName() + "%");
        }
        if (StringUtils.check(assignmentListDTO.getTestType())) {
            results.append("AND tt.test_type_name LIKE :testType\n");
            params.put("testType", "%" + assignmentListDTO.getTestType() + "%");
        }
        if(StringUtils.check(assignmentListDTO.getDoctorName())){
            results.append("AND s.full_name LIKE :doctorName\n");
            params.put("doctorName", "%" + assignmentListDTO.getDoctorName() + "%");
        }
        if(StringUtils.check(assignmentListDTO.getStatus())){
            results.append("AND t.request_status = :status\n");
            params.put("status", RequestStatus.pending.getValue());
        }
        String temp = RequestStatus.pending.getValue();
        return results.toString();
    }

    @Override
    public Page<TestRequestEntity> toGetReceivePatientByStatusPending(AssignmentListDTO assignmentListDTO) throws IllegalAccessException {
        Map<String, Object> params = new HashMap<>();
        String baseQuery = "FROM test_requests t " + joinSearchTestRequest(assignmentListDTO) + whereSearchTestRequest(assignmentListDTO, params);
        String selectQuery = "SELECT t.* " + baseQuery;
        String countQuery = "SELECT COUNT(*) " + baseQuery;

        int page = assignmentListDTO.getPage();
        int size = assignmentListDTO.getSize();
        Pageable pageable = PageRequest.of(page, size);

        Query dataQuery = entityManager.createNativeQuery(selectQuery, TestRequestEntity.class);
        Query countQueryObj = entityManager.createNativeQuery(countQuery);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            dataQuery.setParameter(entry.getKey(), entry.getValue());
            countQueryObj.setParameter(entry.getKey(), entry.getValue());
        }

        dataQuery.setFirstResult(page * size);
        dataQuery.setMaxResults(size);

        List<TestRequestEntity> resultList = dataQuery.getResultList();
        long total = ((Number) countQueryObj.getSingleResult()).longValue();

        return new PageImpl<>(resultList, pageable, total);
    }
}
