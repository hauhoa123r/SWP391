package org.project.repository;

import org.project.entity.CardiacExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardiacRepostiory extends JpaRepository<CardiacExamEntity, Long> {
}
