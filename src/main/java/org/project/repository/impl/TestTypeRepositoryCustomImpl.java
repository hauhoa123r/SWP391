package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.AppointmentEntity;
import org.project.entity.TestTypeEntity;
import org.project.model.dto.SearchTestTypeDTO;
import org.project.model.response.TestRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestTypeRepositoryCustomImpl implements TestTypeRepositoryCustom {


    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<TestTypeEntity> toFilterTestRequestResponse(SearchTestTypeDTO searchTestTypeDTO) {
        StringBuilder sql = new StringBuilder("SELECT * FROM test_types t WHERE 1 = 1\n");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM test_types t WHERE 1 = 1\n");

        if (searchTestTypeDTO.getTestType() != null && !searchTestTypeDTO.getTestType().trim().isEmpty()) {
            sql.append("AND t.test_type_name LIKE CONCAT('%', :testType, '%')\n");
            countSql.append("AND t.test_type_name LIKE CONCAT('%', :testType, '%')\n");
        }
        sql.append("AND t.status = 'active'\n");
        countSql.append("AND t.status = 'active'\n");

        Query query = em.createNativeQuery(sql.toString(), TestTypeEntity.class);
        Query countQuery = em.createNativeQuery(countSql.toString());

        if (searchTestTypeDTO.getTestType() != null && !searchTestTypeDTO.getTestType().trim().isEmpty()) {
            query.setParameter("testType", searchTestTypeDTO.getTestType().trim());
            countQuery.setParameter("testType", searchTestTypeDTO.getTestType().trim());
        }

        query.setFirstResult(searchTestTypeDTO.getPage() * searchTestTypeDTO.getSize());
        query.setMaxResults(searchTestTypeDTO.getSize());

        List<TestTypeEntity> resultList = query.getResultList();
        long total = ((Number) countQuery.getSingleResult()).longValue();
        Pageable pageable = PageRequest.of(searchTestTypeDTO.getPage(), searchTestTypeDTO.getSize());

        return new PageImpl<>(resultList, pageable, total);
    }
}
