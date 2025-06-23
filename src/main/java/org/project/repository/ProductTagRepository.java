package org.project.repository;

import org.project.entity.ProductTagEntity;
import org.project.entity.ProductTagEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTagEntity, ProductTagEntityId> {
    @Query("SELECT DISTINCT pt.id.name FROM ProductTagEntity pt")
    List<String> findDistinctTagNames();
}
