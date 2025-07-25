package org.project.repository;

import org.project.entity.IngredientRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRequestRepository extends JpaRepository<IngredientRequestEntity, Long> {
}
