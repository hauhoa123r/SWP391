# Hướng dẫn sử dụng Search & Sort Specification (Doctor API Example)

## Tổng quan

Hệ thống search & sort sử dụng Specification cho phép xây dựng các truy vấn động, mạnh mẽ, hỗ trợ cả tìm kiếm (search),
sắp xếp (sort), phân trang (pagination) và các phép toán tổng hợp (aggregate) trên Spring Data JPA.

## 1. Các thành phần chính

### a. SearchCriteria

- Đại diện cho một điều kiện tìm kiếm.
- Thuộc tính: `fieldName`, `comparisonOperator` (so sánh), `comparedValue`, `joinType` (LEFT/INNER...)

### b. SortCriteria

- Đại diện cho một điều kiện sắp xếp.
- Thuộc tính: `fieldName`, `aggregationFunction` (NONE, AVG, COUNT...), `sortDirection` (ASC/DESC), `joinType`.

### c. ComparisonOperator & AggregationFunction

- Enum định nghĩa các phép toán so sánh (EQUALS, CONTAINS, GREATER_THAN, ...) và tổng hợp (AVG, COUNT, SUM, ...).

### d. SpecificationUtils

- Hỗ trợ build Specification từ danh sách SearchCriteria và SortCriteria.

## 2. Repository

Repository phải extend `JpaSpecificationExecutor<Entity>` để sử dụng Specification:

```java
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long>, JpaSpecificationExecutor<DoctorEntity> {}
```

## 3. Service: Xây dựng search, sort, phân trang

Ví dụ thực tế (DoctorServiceImpl):

```java
public List<DoctorResponse> getAllByCriteria(DoctorDTO doctorDTO, int index, int size) {
    Pageable pageable = pageUtils.getPageable(index, size);
    List<SearchCriteria> searchCriterias = List.of(
        new SearchCriteria("staffEntity.fullName", ComparisonOperator.CONTAINS, doctorDTO.getStaffEntityFullName(), JoinType.LEFT),
        new SearchCriteria("staffEntity.departmentEntity.id", ComparisonOperator.EQUALS, doctorDTO.getStaffEntityDepartmentEntityId(), JoinType.LEFT),
        new SearchCriteria("staffEntity.reviewEntities", ComparisonOperator.GREATER_THAN_OR_EQUAL_TO, doctorDTO.getMinStarRating(), JoinType.LEFT)
    );
    List<SortCriteria> sortCriterias = switch (doctorDTO.getSortFieldName()) {
        case "staffEntity.fullName" -> List.of(new SortCriteria("staffEntity.fullName", AggregationFunction.NONE, doctorDTO.getSortDirection(), JoinType.LEFT));
        case "staffEntity.reviewEntities.rating" -> List.of(new SortCriteria("staffEntity.reviewEntities.rating", AggregationFunction.AVG, doctorDTO.getSortDirection(), JoinType.LEFT));
        case "staffEntity.reviewEntities.id" -> List.of(new SortCriteria("staffEntity.reviewEntities.id", AggregationFunction.COUNT, doctorDTO.getSortDirection(), JoinType.LEFT));
        default -> List.of();
    };
    Page<DoctorEntity> doctorEntityPage = doctorRepository.findAll(
        specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable
    );
    return doctorEntityPage.map(doctorConverter::toResponse).getContent();
}
```

## 4. Controller: Nhận search + sort từ DTO, trả về kết quả phân trang

```java
@GetMapping("/filter/page/{pageIndex}")
public List<DoctorResponse> getAllDoctors(@PathVariable int pageIndex, @ModelAttribute DoctorDTO doctorDTO) {
    return doctorService.getAllByCriteria(doctorDTO, pageIndex, PAGE_SIZE_FOR_LIST);
}
```

- Truyền các trường search, sort qua DoctorDTO (có thể dùng @ModelAttribute để nhận từ query param).

## 5. DTO mẫu (DoctorDTO)

```java
public class DoctorDTO {
    private String staffEntityFullName;
    private Long staffEntityDepartmentEntityId;
    private Double minStarRating;
    private String sortFieldName; // ví dụ: staffEntity.fullName, staffEntity.reviewEntities.rating
    private AggregationFunction sortDirection; // ASC hoặc DESC
    // ... các trường khác
}
```

## 6. Hướng dẫn sử dụng cho entity khác

- Tạo DTO tương tự DoctorDTO cho entity cần search/sort.
- Tạo SearchCriteria và SortCriteria phù hợp.
- Gọi repository.findAll(specification, pageable) như ví dụ trên.

## 7. Lưu ý

- Các trường search/sort phải đúng tên mapping trong entity.
- Có thể kết hợp nhiều điều kiện search và nhiều sort.
- Hỗ trợ cả aggregate sort (AVG, COUNT, SUM, ...).
- Nên validate input DTO trước khi build criteria.
- Pagination nên dùng để tránh trả về quá nhiều kết quả.

## 8. Ví dụ gọi API

```
GET /api/doctor/filter/page/0?staffEntityFullName=Nguyen&sortFieldName=staffEntity.reviewEntities.rating&sortDirection=DESC
```

## 9. Best Practice

- Tách logic build search/sort ra service/utils.
- Luôn kiểm tra null cho các trường search/sort.
- Sử dụng Enum cho sortFieldName, sortDirection ở phía FE để tránh lỗi chính tả.
- Có thể mở rộng thêm các phép toán so sánh/tổng hợp nếu cần.

---

**Hệ thống search & sort này giúp API linh hoạt, dễ mở rộng, dễ bảo trì và tối ưu cho các use-case thực tế!**
