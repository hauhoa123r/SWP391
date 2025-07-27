package org.project.repository;

import org.project.entity.SampleEntity;
import org.project.entity.TestRequestEntity;
import org.project.repository.impl.SampleFilterNameRepositoryCustom;
import org.project.repository.impl.SampleScheduleCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SampleScheduleRepository extends JpaRepository<SampleEntity, Long>, SampleScheduleCustom, SampleFilterNameRepositoryCustom {
    Page<SampleEntity> findBySampleStatus(String sampleStatus, Pageable pageable);

    SampleEntity findByTestRequest_Id(Long testRequestId);
}
