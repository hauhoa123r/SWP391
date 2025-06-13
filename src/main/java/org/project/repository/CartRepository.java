package org.project.repository;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CartRepository extends JpaRepository<CartItemEntity, CartItemEntityId>{

}
