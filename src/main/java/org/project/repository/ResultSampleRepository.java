package org.project.repository;

import org.project.entity.ResultEntity;
import org.project.repository.impl.ResultSampleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultSampleRepository extends JpaRepository<ResultEntity, Long>, ResultSampleRepositoryCustom {
    Optional<ResultEntity> findById(Long id);
}
