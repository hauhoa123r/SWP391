package org.project.admin.dto.request;

import org.project.admin.enums.coupon.CouponStatus;
import org.project.admin.enums.coupon.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class CouponRequest {

    @NotBlank(message = "Mã coupon không được để trống")
    @Size(min = 3, max = 20, message = "Mã coupon phải từ 3 đến 20 ký tự")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã coupon chỉ được chứa chữ hoa, số, dấu gạch dưới và gạch ngang")
    private String code;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 10, max = 500, message = "Mô tả phải từ 10 đến 500 ký tự")
    private String description;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm giá phải lớn hơn 0")
    @DecimalMax(value = "999999.99", message = "Giá trị giảm giá không được vượt quá 999,999.99")
    private BigDecimal value;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải là ngày trong tương lai")
    private LocalDate expirationDate;

    @FutureOrPresent(message = "Ngày bắt đầu phải là hôm nay hoặc trong tương lai")
    private LocalDate startDate;

    @DecimalMin(value = "0", message = "Số tiền tối thiểu không được âm")
    private BigDecimal minOrderAmount;

    @Min(value = 1, message = "Số lần sử dụng tối đa phải lớn hơn 0")
    private Integer usageLimit;

    @Min(value = 1, message = "Số lần sử dụng của mỗi user phải lớn hơn 0")
    private Integer userUsageLimit;

    @Min(value = 0, message = "Số lần đã sử dụng không được âm")
    private Integer usedCount;

    private CouponStatus status;
}
