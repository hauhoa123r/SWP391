package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.project.entity.StaffEntity;
import org.project.repository.StaffRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StaffRepositoryImpl implements StaffRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<StaffEntity> searchStaffs(Pageable pageable, String field, String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StaffEntity> query = cb.createQuery(StaffEntity.class);
        Root<StaffEntity> staffRoot = query.from(StaffEntity.class);

        // Tạo danh sách các điều kiện tìm kiếm
        List<Predicate> predicates = new ArrayList<>();

        // Thêm điều kiện tìm kiếm theo field và keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (field) {
                case "id":
                    predicates.add(cb.like(
                        cb.lower(staffRoot.get("id").as(String.class)),
                        "%" + keyword.toLowerCase() + "%"
                    ));
                    break;
                case "fullName":
                    predicates.add(cb.like(
                        cb.lower(staffRoot.get("fullName")),
                        "%" + keyword.toLowerCase() + "%"
                    ));
                    break;
                case "email":
                    predicates.add(cb.like(
                        cb.lower(staffRoot.get("userEntity").get("email")),
                        "%" + keyword.toLowerCase() + "%"
                    ));
                    break;
                case "phoneNumber":
                    predicates.add(cb.like(
                        cb.lower(staffRoot.get("userEntity").get("phoneNumber")),
                        "%" + keyword.toLowerCase() + "%"
                    ));
                    break;
                default:
                    // Tìm kiếm theo tất cả các trường
                    predicates.add(cb.or(
                        cb.like(cb.lower(staffRoot.get("fullName")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(staffRoot.get("userEntity").get("email")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(staffRoot.get("userEntity").get("phoneNumber")), "%" + keyword.toLowerCase() + "%")
                    ));
                    break;
            }
        }

        // Xây dựng query
        query.where(predicates.toArray(Predicate[]::new));
        query.orderBy(cb.asc(staffRoot.get("id")));

        // Tạo query để đếm số lượng kết quả
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<StaffEntity> countRoot = countQuery.from(StaffEntity.class);
        countQuery.select(cb.count(countRoot));

        // Xây dựng predicate cho countRoot (không được dùng predicate của staffRoot)
        List<Predicate> countPredicates = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            switch (field) {
                case "fullName":
                    countPredicates.add(cb.like(cb.lower(countRoot.get("fullName")), "%" + keyword.toLowerCase() + "%"));
                    break;
                case "email":
                    countPredicates.add(cb.like(cb.lower(countRoot.get("userEntity").get("email")), "%" + keyword.toLowerCase() + "%"));
                    break;
                case "phoneNumber":
                    countPredicates.add(cb.like(cb.lower(countRoot.get("userEntity").get("phoneNumber")), "%" + keyword.toLowerCase() + "%"));
                    break;
                default:
                    countPredicates.add(cb.or(
                            cb.like(cb.lower(countRoot.get("fullName")), "%" + keyword.toLowerCase() + "%"),
                            cb.like(cb.lower(countRoot.get("userEntity").get("email")), "%" + keyword.toLowerCase() + "%"),
                            cb.like(cb.lower(countRoot.get("userEntity").get("phoneNumber")), "%" + keyword.toLowerCase() + "%")
                    ));
                    break;
            }
        }
        countQuery.where(countPredicates.toArray(Predicate[]::new));

        // Thực thi các query
        TypedQuery<StaffEntity> result = entityManager.createQuery(query);
        TypedQuery<Long> count = entityManager.createQuery(countQuery);

        // Áp dụng phân trang
        result.setFirstResult((int) pageable.getOffset());
        result.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(result.getResultList(), pageable, count.getSingleResult());
    }

    @Override
    public Page<StaffEntity> searchStaffs(Pageable pageable, String keyword) {
        return searchStaffs(pageable, "", keyword);
    }
}
