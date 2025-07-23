//package org.project.repository;
//
//import org.project.entity.CartItemEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
//    List<CartItemEntity> findByUserEntity_Id(Long userId);
//
//    Optional<CartItemEntity> findByUserEntity_IdAndProductEntity_Id(Long userId, Long productId);
//
//    void deleteByUserEntity_IdAndProductEntity_Id(Long userId, Long productId);
//
//    void deleteByUserEntity_Id(Long userId);
//
//    int countByUserEntity_Id(Long userId);
//
//    @Query("SELECT MAX(c.id.cartItemId) FROM CartItemEntity c WHERE c.id.userId = :userId")
//    Optional<Long> findMaxCartItemIdByUserId(@Param("userId") Long userId);
//}