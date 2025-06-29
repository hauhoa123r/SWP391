package org.project.specification;

import jakarta.persistence.criteria.Predicate;
import org.project.entity.PatientEntity;
import org.project.model.request.AdminPatientRequest;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

public class AdminPatientSpecification {
   public static Specification<PatientEntity> filter(AdminPatientRequest req) {
       return (root, query, cb) -> {
           Predicate predicate = cb.conjunction();  // Tạo điều kiện mặc định

           // Kiểm tra từ khóa (keyword) và truy vấn
           if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
               String pattern = "%" + req.getKeyword().toLowerCase() + "%";
               predicate = cb.and(predicate, cb.or(
                       cb.like(cb.lower(root.get("fullName")), pattern),
                       cb.like(cb.lower(root.get("email")), pattern),
                       cb.like(cb.lower(root.get("phoneNumber")), pattern)
               ));
           }

           // Truy vấn theo patientId, nếu có trong yêu cầu
           if (req.getPatientId() != null) {
               predicate = cb.and(predicate, cb.equal(root.get("id"), req.getPatientId()));
           }

           // Truy vấn theo userEntity.id, nếu có trong yêu cầu
           if (req.getUserId() != null) {
               predicate = cb.and(predicate, cb.equal(root.get("userEntity").get("id"), req.getUserId()));  // Sửa lại đây
           }

           // Lọc theo giới tính, nếu có trong yêu cầu
           if (req.getGender() != null) {
               predicate = cb.and(predicate, cb.equal(root.get("gender"), req.getGender()));
           }

           // Lọc theo quan hệ gia đình, nếu có trong yêu cầu
           if (req.getFamilyRelationship() != null) {
               predicate = cb.and(predicate, cb.equal(root.get("familyRelationship"), req.getFamilyRelationship()));
           }

           // Lọc theo nhóm máu, nếu có trong yêu cầu
           if (req.getBloodType() != null) {
               predicate = cb.and(predicate, cb.equal(root.get("bloodType"), req.getBloodType()));
           }

           // Lọc theo ngày sinh từ
           if (req.getBirthdateFrom() != null) {
               predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("birthdate"), Date.valueOf(req.getBirthdateFrom())));
           }

           // Lọc theo ngày sinh đến
           if (req.getBirthdateTo() != null) {
               predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("birthdate"), Date.valueOf(req.getBirthdateTo())));
           }

           // Lọc theo địa chỉ
           if (req.getAddress() != null && !req.getAddress().isBlank()) {
               String pattern = "%" + req.getAddress().toLowerCase() + "%";
               predicate = cb.and(predicate, cb.like(cb.lower(root.get("address")), pattern));
           }

           return predicate;
       };
   }
}
