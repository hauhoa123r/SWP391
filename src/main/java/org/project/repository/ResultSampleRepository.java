package org.project.repository;

import org.project.entity.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultSampleRepository extends JpaRepository<ResultEntity, Long> {
}
