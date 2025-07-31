package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.AppointmentEntity;
import org.project.entity.MedicineEntity;
import org.project.entity.ProductEntity;
import org.project.model.dto.AppointmentFilterDTO;
import org.project.model.dto.DoctorMedicineDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorMedicineRepositoryCustomImpl implements DoctorMedicineRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ProductEntity> toFilterMedicine(DoctorMedicineDTO doctorMedicineDTO) {
        StringBuilder sql = new StringBuilder("select * from products p\n");
        sql.append("WHERE 1 = 1\n");
        sql.append(whereSearchAppointment(doctorMedicineDTO));

        Query query = em.createNativeQuery(sql.toString(), ProductEntity.class);

        query.setFirstResult(doctorMedicineDTO.getPage() * doctorMedicineDTO.getSize());
        query.setMaxResults(doctorMedicineDTO.getSize());

        List<ProductEntity> resultList = query.getResultList();

        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM products p\n");
        countSql.append("WHERE 1 = 1\n");
        countSql.append(whereSearchAppointment(doctorMedicineDTO));

        Query countQuery = em.createNativeQuery(countSql.toString());

        long total = ((Number) countQuery.getSingleResult()).longValue();
        Pageable pageable = PageRequest.of(doctorMedicineDTO.getPage(), doctorMedicineDTO.getSize());

        return new PageImpl<>(resultList, pageable, total);
    }

    private String whereSearchAppointment(DoctorMedicineDTO doctorMedicineDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("AND p.product_type = 'MEDICINE'\n");
        if(doctorMedicineDTO.getMedicineName() != null && !doctorMedicineDTO.getMedicineName().isEmpty()){
            sb.append("AND p.name like '%"+doctorMedicineDTO.getMedicineName()+"%'");
        }
        return sb.toString();
    }
}
