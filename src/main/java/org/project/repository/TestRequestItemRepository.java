package org.project.repository;

import org.project.entity.TestRequestItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface TestRequestItemRepository extends JpaRepository<TestRequestItemEntity, Long> {

}
