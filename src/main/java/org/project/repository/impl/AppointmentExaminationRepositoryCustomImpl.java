package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.AppointmentEntity;
import org.project.model.dto.AppointmentFilterDTO;
import org.project.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppointmentExaminationRepositoryCustomImpl implements AppointmentExaminationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<AppointmentEntity> toFilterAppointmentByDoctorIdAndPatientName(AppointmentFilterDTO dto) {
        StringBuilder sql = new StringBuilder("SELECT a.* FROM appointments a\n");
        sql.append(joinSearchAppointment(dto));
        sql.append("WHERE 1 = 1\n");
        sql.append(whereSearchAppointment(dto));
        sql.append(" ORDER BY a.start_time ASC");

        Query query = entityManager.createNativeQuery(sql.toString(), AppointmentEntity.class);
        setParameters(query, dto);

        query.setFirstResult(dto.getPage() * dto.getSize());
        query.setMaxResults(dto.getSize());

        List<AppointmentEntity> resultList = query.getResultList();

        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM appointments a\n");
        countSql.append(joinSearchAppointment(dto));
        countSql.append("WHERE 1 = 1\n");
        countSql.append(whereSearchAppointment(dto));

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        setParameters(countQuery, dto);

        long total = ((Number) countQuery.getSingleResult()).longValue();
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());

        return new PageImpl<>(resultList, pageable, total);
    }

    private String joinSearchAppointment(AppointmentFilterDTO dto) {
        StringBuilder sb = new StringBuilder();
        if(dto.getName() != null && !dto.getName().isEmpty()){
            sb.append("JOIN patients p ON p.patient_id = a.patient_id\n");
        }
        return sb.toString();
    }

    private String whereSearchAppointment(AppointmentFilterDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("AND DATE(a.start_time) = CURRENT_DATE\n");
        sb.append("AND a.appointment_status = '" + dto.getStatus() + "'\n");
        sb.append("AND a.doctor_id = :doctorId\n");
        if(dto.getName() != null && !dto.getName().isEmpty()){
            sb.append("AND LOWER(p.full_name) LIKE LOWER(CONCAT('%', :name, '%'))\n");
        }
        return sb.toString();
    }

    private void setParameters(Query query, AppointmentFilterDTO dto) {
        query.setParameter("doctorId", dto.getDoctorId());
        if(dto.getName() != null && !dto.getName().isEmpty()){
            query.setParameter("name", dto.getName());
        }
    }
}
