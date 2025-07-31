package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.CouponEntity;
import org.project.model.dto.CouponDTO;
import org.project.repository.CouponRepository;
import org.project.service.CouponService;
import org.project.service.UserCouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponService userCouponService;

    @Override
    public Page<CouponDTO> findAllCoupons(int page, int size, String sortBy, String sortDir) {
        Sort sort = createSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CouponEntity> couponPage = couponRepository.findAll(pageable);
        return couponPage.map(this::convertToDTO);
    }

    @Override
    public Optional<CouponDTO> findCouponById(Long id) {
        return couponRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public Optional<CouponDTO> findCouponByCode(String code) {
        return couponRepository.findByCode(code).map(this::convertToDTO);
    }

    @Override
    public Page<CouponDTO> searchCoupons(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = createSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CouponEntity> couponPage;
        if (keyword != null && !keyword.isEmpty()) {
            couponPage = couponRepository.findByKeyword(keyword.toLowerCase(), pageable);
        } else {
            couponPage = couponRepository.findAll(pageable);
        }
        
        return couponPage.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public CouponDTO createCoupon(CouponDTO couponDTO) {
        if (isCouponCodeExists(couponDTO.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists: " + couponDTO.getCode());
        }
        
        CouponEntity entity = convertToEntity(couponDTO);
        CouponEntity savedEntity = couponRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    @Transactional
    public CouponDTO updateCoupon(Long id, CouponDTO couponDTO) {
        CouponEntity existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found with ID: " + id));
        
        // Kiểm tra nếu code thay đổi và code mới đã tồn tại
        if (!existingCoupon.getCode().equals(couponDTO.getCode()) && 
            couponRepository.existsByCode(couponDTO.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists: " + couponDTO.getCode());
        }
        
        // Cập nhật thông tin
        existingCoupon.setCode(couponDTO.getCode());
        existingCoupon.setDescription(couponDTO.getDescription());
        existingCoupon.setValue(couponDTO.getValue());
        existingCoupon.setMinimumOrderAmount(couponDTO.getMinimumOrderAmount());
        existingCoupon.setExpirationDate(couponDTO.getExpirationDate());
        existingCoupon.setDiscountType(couponDTO.getDiscountType());
        
        CouponEntity updatedEntity = couponRepository.save(existingCoupon);
        return convertToDTO(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new IllegalArgumentException("Coupon not found with ID: " + id);
        }
        
        couponRepository.deleteById(id);
    }

    @Override
    public boolean isCouponCodeExists(String code) {
        return couponRepository.existsByCode(code);
    }

    @Override
    public Page<CouponDTO> findValidCoupons(int page, int size, String sortBy, String sortDir) {
        Sort sort = createSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Date today = new Date(System.currentTimeMillis());
        Page<CouponEntity> couponPage = couponRepository.findByExpirationDateGreaterThanEqual(today, pageable);
        
        return couponPage.map(this::convertToDTO);
    }

    @Override
    public Page<CouponDTO> findExpiredCoupons(int page, int size, String sortBy, String sortDir) {
        Sort sort = createSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Date today = new Date(System.currentTimeMillis());
        Page<CouponEntity> couponPage = couponRepository.findByExpirationDateLessThan(today, pageable);
        
        return couponPage.map(this::convertToDTO);
    }
    
    /**
     * Tạo Sort object dựa trên các tham số
     */
    private Sort createSort(String sortBy, String sortDir) {
        String sortField = "id";  // Default sort field
        
        // Validate sort field
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "code":
                    sortField = "code";
                    break;
                case "value":
                    sortField = "value";
                    break;
                case "expiration":
                    sortField = "expirationDate";
                    break;
            }
        }
        
        Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
                
        return Sort.by(direction, sortField);
    }
    
    /**
     * Chuyển đổi từ Entity sang DTO
     */
    private CouponDTO convertToDTO(CouponEntity entity) {
        if (entity == null) {
            return null;
        }
        
        CouponDTO dto = new CouponDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setValue(entity.getValue());
        dto.setMinimumOrderAmount(entity.getMinimumOrderAmount());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setDiscountType(entity.getDiscountType());
        
        // Tính toán các trường bổ sung
        Date today = new Date(System.currentTimeMillis());
        dto.setValid(entity.getExpirationDate() != null && !entity.getExpirationDate().before(today));
        
        // Get actual usage count from UserCouponService
        dto.setUsageCount(userCouponService.getCouponUsageCount(entity.getId()));
        
        return dto;
    }
    
    /**
     * Chuyển đổi từ DTO sang Entity
     */
    private CouponEntity convertToEntity(CouponDTO dto) {
        if (dto == null) {
            return null;
        }
        
        CouponEntity entity = new CouponEntity();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setValue(dto.getValue());
        entity.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        entity.setExpirationDate(dto.getExpirationDate());
        entity.setDiscountType(dto.getDiscountType());
        
        return entity;
    }
} 