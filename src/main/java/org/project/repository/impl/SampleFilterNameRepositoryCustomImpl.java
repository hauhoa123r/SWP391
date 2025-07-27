package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.project.entity.TestTypeEntity;
import org.project.model.dto.FilterSampleNameDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SampleFilterNameRepositoryCustomImpl implements SampleFilterNameRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    private String whereSearchTestRequest(FilterSampleNameDTO filterSampleNameDTO, Map<String, Object> params) throws IllegalAccessException {
        StringBuilder results = new StringBuilder("WHERE 1 = 1\n");
        if(filterSampleNameDTO.getSampleName() != null && !filterSampleNameDTO.getSampleName().isEmpty()) {
            results.append("AND t.test_type_name LIKE :sampleName\n");
            params.put("sampleName", "%" + filterSampleNameDTO.getSampleName() + "%");
        }
        results.append("AND t.status = :sampleStatus\n");
        params.put("sampleStatus", filterSampleNameDTO.getStatus());
        return results.toString();
    }

    @Override
    public Page<TestTypeEntity> filterNameSample(FilterSampleNameDTO filterSampleNameDTO) throws IllegalAccessException {
        Map<String, Object> params = new HashMap<>();
        String baseQuery = "FROM test_types t" + "\n" + whereSearchTestRequest(filterSampleNameDTO, params);
        String selectQuery = "SELECT t.* " + baseQuery;
        String countQuery = "SELECT COUNT(*) " + baseQuery;
        int page = filterSampleNameDTO.getPage();
        int size = filterSampleNameDTO.getSize();

        Pageable pageRequest = PageRequest.of(page, size);
        Query dataQuery = entityManager.createNativeQuery(selectQuery, TestTypeEntity.class);
        Query countQueryObj = entityManager.createNativeQuery(countQuery);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            dataQuery.setParameter(entry.getKey(), entry.getValue());
            countQueryObj.setParameter(entry.getKey(), entry.getValue());
        }
        dataQuery.setFirstResult(page * size);
        dataQuery.setMaxResults(size);
        List<TestTypeEntity> resultList = dataQuery.getResultList();
        long total = ((Number) countQueryObj.getSingleResult()).longValue();

        return new PageImpl<>(resultList, pageRequest, total);
    }
}
