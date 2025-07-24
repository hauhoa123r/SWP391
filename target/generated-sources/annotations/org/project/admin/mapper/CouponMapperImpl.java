package org.project.admin.mapper;

import javax.annotation.processing.Generated;
import org.project.admin.dto.request.CouponRequest;
import org.project.admin.dto.response.CouponResponse;
import org.project.admin.entity.Coupon;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-24T11:18:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class CouponMapperImpl implements CouponMapper {

    @Override
    public Coupon toEntity(CouponRequest request) {
        if ( request == null ) {
            return null;
        }

        Coupon coupon = new Coupon();

        coupon.setCode( request.getCode() );
        coupon.setDescription( request.getDescription() );
        coupon.setDiscountType( request.getDiscountType() );
        coupon.setValue( request.getValue() );
        coupon.setStartDate( request.getStartDate() );
        coupon.setExpirationDate( request.getExpirationDate() );
        coupon.setStatus( request.getStatus() );
        coupon.setUsageLimit( request.getUsageLimit() );
        coupon.setUsedCount( request.getUsedCount() );
        coupon.setUserUsageLimit( request.getUserUsageLimit() );
        coupon.setMinOrderAmount( request.getMinOrderAmount() );

        return coupon;
    }

    @Override
    public CouponResponse toResponse(Coupon coupon) {
        if ( coupon == null ) {
            return null;
        }

        CouponResponse couponResponse = new CouponResponse();

        couponResponse.setCouponId( coupon.getCouponId() );
        couponResponse.setCode( coupon.getCode() );
        couponResponse.setDescription( coupon.getDescription() );
        couponResponse.setDiscountType( coupon.getDiscountType() );
        couponResponse.setValue( coupon.getValue() );
        couponResponse.setStartDate( coupon.getStartDate() );
        couponResponse.setExpirationDate( coupon.getExpirationDate() );
        couponResponse.setUsageLimit( coupon.getUsageLimit() );
        couponResponse.setUserUsageLimit( coupon.getUserUsageLimit() );
        couponResponse.setUsedCount( coupon.getUsedCount() );
        couponResponse.setMinOrderAmount( coupon.getMinOrderAmount() );
        couponResponse.setStatus( coupon.getStatus() );

        return couponResponse;
    }
}
