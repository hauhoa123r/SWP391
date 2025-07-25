package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponDTO {
    private Long userId;
    private Long couponId;
    private String couponCode;
    private String username;
    private Timestamp useAt;
} 