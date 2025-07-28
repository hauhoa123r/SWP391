package org.project.repository;

import org.project.entity.AppointmentEntity;
import org.project.repository.impl.ResultTestRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultTestRepository extends JpaRepository<AppointmentEntity, Long>, ResultTestRepositoryCustom {
}