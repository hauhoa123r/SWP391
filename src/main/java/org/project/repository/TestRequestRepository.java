package org.project.repository;

import org.project.entity.TestRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRequestRepository extends JpaRepository<TestRequestEntity, Long> {

}
