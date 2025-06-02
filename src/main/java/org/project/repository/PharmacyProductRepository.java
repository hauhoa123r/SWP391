package org.project.repository;

import org.project.entity.PharmacyProductEntity;

public interface PharmacyProductRepository {
	public PharmacyProductEntity getById(long id);
}
