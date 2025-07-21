package org.project.repository;

import org.project.entity.OrderItem;
import org.project.entity.OrderItemId;
import org.project.model.response.TopProductDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderItemRepository extends CrudRepository<OrderItem, OrderItemId> {

    @Query("""
        SELECT new org.project.model.response.TopProductDTO(
            p.productId, p.name, SUM(p.price * oi.quantity)
        )
        FROM OrderItem oi
        JOIN oi.product p
        GROUP BY p.productId, p.name
        ORDER BY SUM(p.price * oi.quantity) DESC
    """)
    List<TopProductDTO> findTopProductsByRevenue(Pageable pageable);
}
