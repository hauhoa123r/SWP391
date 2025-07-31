package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.AppointmentEntity;
import org.project.model.dai.AppointmentDAI;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentRepositoryCustomImpl implements AppointmentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private String whereSearchAppointment(AppointmentDAI appointmentDAI) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("WHERE 1 = 1\n");
        stringBuilder.append("AND h.name LIKE '%").append(appointmentDAI.getHospitalName()).append("%'\n");
        stringBuilder.append("AND d.name LIKE '%").append(appointmentDAI.getDepartmentName()).append("%'\n");

        if (appointmentDAI.getDoctorName() != null) {
            stringBuilder.append("AND s.full_name LIKE '%").append(appointmentDAI.getDoctorName()).append("%'\n");
        }

        stringBuilder.append("AND DATE(a.start_time) = '").append(appointmentDAI.getDate()).append("'\n");
        stringBuilder.append("AND HOUR(a.start_time) = ").append(appointmentDAI.getTime()).append("\n");

        return stringBuilder.toString();
    }

    private String joinSearchAppointment(AppointmentDAI appointmentDAI) {
        String stringBuilder = "JOIN staffs s ON a.doctor_id = s.staff_id\n" +
                "JOIN hospitals h ON s.hospital_id = h.hospital_id\n" +
                "JOIN departments d ON s.department_id = d.department_id\n" +
                "JOIN services se ON a.service_id = se.service_id\n";
        return stringBuilder;
    }

    @Override
    public AppointmentEntity searchAppointmentExist(AppointmentDAI appointmentDai) {
        StringBuilder sql = new StringBuilder("SELECT a.* FROM appointments a\n");
        sql.append(joinSearchAppointment(appointmentDai));
        sql.append(whereSearchAppointment(appointmentDai));

        try {
            Query query = entityManager.createNativeQuery(sql.toString(), AppointmentEntity.class);
            return (AppointmentEntity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
