package org.project.service;

import java.math.BigDecimal;

import org.project.exception.CouponException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public interface CouponService {
	BigDecimal applyCoupon(String code, Long userId, HttpSession session) throws CouponException;
}
