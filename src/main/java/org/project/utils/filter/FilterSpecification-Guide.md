# Hướng dẫn sử dụng FilterSpecification

## Tổng quan

`FilterSpecification` là một utility class được thiết kế để tạo các điều kiện lọc (filter) động cho Spring Data JPA Specification. Nó cho phép bạn xây dựng các query phức tạp một cách dễ dàng và linh hoạt.

## Cấu trúc hệ thống Filter

### 1. SearchCriteria

Đây là class chứa thông tin về một điều kiện tìm kiếm:

```java
public class SearchCriteria {
    private String fieldName;      // Tên trường cần filter
    private Operation operation;   // Phép toán (EQUALS, CONTAINS, etc.)
    private Object comparedValue;  // Giá trị để so sánh
}
```

### 2. Operation Enum

Định nghĩa các phép toán có thể sử dụng:

-   **EQUALS**: Bằng
-   **NOT_EQUALS**: Khác
-   **GREATER_THAN**: Lớn hơn
-   **LESS_THAN**: Nhỏ hơn
-   **GREATER_THAN_OR_EQUAL_TO**: Lớn hơn hoặc bằng
-   **LESS_THAN_OR_EQUAL_TO**: Nhỏ hơn hoặc bằng
-   **LIKE**: Giống (pattern matching)
-   **NOT_LIKE**: Không giống
-   **CONTAINS**: Chứa (tự động thêm % ở đầu và cuối)
-   **NOT_CONTAINS**: Không chứa
-   **IN**: Có trong danh sách
-   **NOT_IN**: Không có trong danh sách
-   **IS_NULL**: Là null
-   **IS_NOT_NULL**: Không null
-   **BETWEEN**: Trong khoảng
-   **NOT_BETWEEN**: Không trong khoảng
-   **AND**: Phép AND (dùng để kết hợp điều kiện)
-   **OR**: Phép OR (dùng để kết hợp điều kiện)

### 3. FilterSpecification

Class chính để xây dựng các điều kiện filter.

## Cách sử dụng

### 1. Tạo Repository extend JpaSpecificationExecutor

**Quan trọng**: Repository của bạn phải extend `JpaSpecificationExecutor` để có thể sử dụng Specification:

```java
@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long>, JpaSpecificationExecutor<StaffEntity> {
    // Các custom method khác (nếu có)
}
```

**Ví dụ các Repository khác:**

```java
@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long>, JpaSpecificationExecutor<PatientEntity> {
}

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>, JpaSpecificationExecutor<AppointmentEntity> {
}

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long>, JpaSpecificationExecutor<DepartmentEntity> {
}
```

**Lưu ý**:

-   `JpaSpecificationExecutor<T>` cung cấp các method như `findAll(Specification<T> spec)`, `findOne(Specification<T> spec)`, `count(Specification<T> spec)`
-   Nếu không extend interface này, bạn sẽ không thể sử dụng FilterSpecification

### 3. Inject FilterSpecification vào Controller/Service

```java
@RestController
public class YourController {

    @Autowired
    private FilterSpecification<YourEntity> filterSpecification;

    @Autowired
    private YourRepository yourRepository; // Repository phải extend JpaSpecificationExecutor
}
```

### 4. Xây dựng điều kiện filter

#### Cách 1: Sử dụng addSearchCriteria (Recommended)

```java
// Tạo một điều kiện filter mới
filterSpecification = filterSpecification
    .addSearchCriteria(new SearchCriteria("fieldName", Operation.EQUALS, value), Operation.AND);
```

#### Cách 2: Sử dụng getSpecification

```java
// Tạo specification từ SearchCriteria
Specification<YourEntity> spec = filterSpecification
    .getSpecification(new SearchCriteria("fieldName", Operation.EQUALS, value));

// Hoặc sử dụng trực tiếp
Specification<YourEntity> spec = filterSpecification
    .getSpecification("fieldName", Operation.EQUALS, value);
```

#### Cách 3: Sử dụng getSpecifications để kết hợp nhiều điều kiện

```java
// Kết hợp nhiều điều kiện với AND
List<SearchCriteria> criterias = Arrays.asList(
    new SearchCriteria("name", Operation.CONTAINS, "John"),
    new SearchCriteria("age", Operation.GREATER_THAN, 25)
);
Specification<YourEntity> spec = filterSpecification.getSpecifications(criterias);
```

### 5. Sử dụng với Repository

```java
// Áp dụng filter vào query
List<YourEntity> results = yourRepository.findAll(filterSpecification.getSpecification());
```

## Ví dụ thực tế: DoctorAPI

Dưới đây là ví dụ từ `DoctorAPI` trong project:

```java
@GetMapping
public List<StaffResponse> getDoctors(@RequestBody StaffDTO staffDTO) {
    // Lọc chỉ những staff có role là DOCTOR
    filterSpecification = filterSpecification
        .addSearchCriteria(new SearchCriteria("staffRole", Operation.EQUALS, DOCTOR_ROLE), Operation.AND);

    // Lọc theo tên (tìm kiếm gần đúng)
    filterSpecification = filterSpecification
        .addSearchCriteria(new SearchCriteria("fullName", Operation.CONTAINS, staffDTO.getFullName()), Operation.AND);

    // Lọc theo tên department (truy cập nested field)
    filterSpecification = filterSpecification
        .addSearchCriteria(new SearchCriteria("departmentEntity.name", Operation.EQUALS, staffDTO.getDepartmentEntityName()), Operation.AND);

    // Lọc theo tên hospital (truy cập nested field)
    filterSpecification = filterSpecification
        .addSearchCriteria(new SearchCriteria("hospitalEntity.name", Operation.EQUALS, staffDTO.getHospitalEntityName()), Operation.AND);

    // Thực thi query và chuyển đổi kết quả
    return staffRepository.findAll(filterSpecification.getSpecification())
        .stream()
        .map(staffConverter::toResponse)
        .toList();
}
```

## Các tính năng nâng cao

### 1. Truy cập Nested Fields

Bạn có thể truy cập các trường lồng nhau bằng cách sử dụng dấu chấm (.):

```java
// Truy cập trường name của departmentEntity
new SearchCriteria("departmentEntity.name", Operation.EQUALS, "Cardiology")

// Truy cập trường nested sâu hơn
new SearchCriteria("user.profile.address.city", Operation.EQUALS, "Ho Chi Minh")
```

### 2. Sử dụng phép OR

```java
filterSpecification = filterSpecification
    .addSearchCriteria(new SearchCriteria("status", Operation.EQUALS, "ACTIVE"), Operation.OR)
    .addSearchCriteria(new SearchCriteria("status", Operation.EQUALS, "PENDING"), Operation.OR);
```

### 3. Sử dụng phép BETWEEN

```java
// Lọc theo khoảng tuổi
Object[] ageRange = {25, 65};
filterSpecification = filterSpecification
    .addSearchCriteria(new SearchCriteria("age", Operation.BETWEEN, ageRange), Operation.AND);
```

### 4. Sử dụng phép IN

```java
// Lọc theo danh sách ID
List<Long> ids = Arrays.asList(1L, 2L, 3L);
filterSpecification = filterSpecification
    .addSearchCriteria(new SearchCriteria("id", Operation.IN, ids), Operation.AND);
```

### 5. Kiểm tra NULL

```java
// Lọc những record có email không null
filterSpecification = filterSpecification
    .addSearchCriteria(new SearchCriteria("email", Operation.IS_NOT_NULL, null), Operation.AND);
```

## Ví dụ hoàn chỉnh khác

```java
@GetMapping("/patients")
public List<PatientResponse> getPatients(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) Integer minAge,
    @RequestParam(required = false) Integer maxAge,
    @RequestParam(required = false) String department) {

    FilterSpecification<PatientEntity> filter = new FilterSpecification<>();

    // Tìm kiếm theo tên (nếu có)
    if (name != null && !name.isEmpty()) {
        filter = filter.addSearchCriteria(
            new SearchCriteria("fullName", Operation.CONTAINS, name),
            Operation.AND);
    }

    // Lọc theo độ tuổi tối thiểu
    if (minAge != null) {
        filter = filter.addSearchCriteria(
            new SearchCriteria("age", Operation.GREATER_THAN_OR_EQUAL_TO, minAge),
            Operation.AND);
    }

    // Lọc theo độ tuổi tối đa
    if (maxAge != null) {
        filter = filter.addSearchCriteria(
            new SearchCriteria("age", Operation.LESS_THAN_OR_EQUAL_TO, maxAge),
            Operation.AND);
    }

    // Lọc theo department
    if (department != null && !department.isEmpty()) {
        filter = filter.addSearchCriteria(
            new SearchCriteria("department.name", Operation.EQUALS, department),
            Operation.AND);
    }

    return patientRepository.findAll(filter.getSpecification())
        .stream()
        .map(patientConverter::toResponse)
        .toList();
}
```

## Lưu ý quan trọng

1. **Null Safety**: Hệ thống đã xử lý null safety. Nếu SearchCriteria có giá trị null, nó sẽ trả về `Specification.allOf()` (không filter gì).

2. **Field Name**: Đảm bảo tên field chính xác và tồn tại trong Entity.

3. **Data Type**: Đảm bảo kiểu dữ liệu của `comparedValue` phù hợp với field trong Entity.

4. **Performance**: Với các query phức tạp, hãy đảm bảo database có index phù hợp.

5. **Dependency Injection**: `FilterSpecification` được đánh dấu `@Component`, nên có thể inject trực tiếp.

## Troubleshooting

### Lỗi thường gặp:

1. **Field không tồn tại**: Kiểm tra tên field trong Entity
2. **Kiểu dữ liệu không khớp**: Đảm bảo `comparedValue` có kiểu dữ liệu đúng
3. **Nested field không được tìm thấy**: Kiểm tra relationship và tên field nested

### Debug tips:

-   Sử dụng logging để xem SQL query được generate
-   Test từng điều kiện một cách riêng biệt
-   Kiểm tra Entity mapping và relationships

## Kết luận

`FilterSpecification` cung cấp một cách linh hoạt và mạnh mẽ để xây dựng các query động trong Spring Data JPA. Nó đặc biệt hữu ích cho các API cần hỗ trợ nhiều tiêu chí tìm kiếm và lọc khác nhau.
