package org.project.admin.mapper;

import javax.annotation.processing.Generated;
import org.project.admin.dto.response.CouponProductResponse;
import org.project.admin.entity.Coupon;
import org.project.admin.entity.CouponProduct;
import org.project.admin.entity.Product;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-24T11:18:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class CouponProductMapperImpl implements CouponProductMapper {

    @Override
    public CouponProductResponse toResponse(CouponProduct entity) {
        if ( entity == null ) {
            return null;
        }

        CouponProductResponse couponProductResponse = new CouponProductResponse();

        couponProductResponse.setCouponId( entityCouponCouponId( entity ) );
        couponProductResponse.setCouponCode( entityCouponCode( entity ) );
        couponProductResponse.setProductId( entityProductProductId( entity ) );
        couponProductResponse.setProductName( entityProductName( entity ) );
        couponProductResponse.setId( entity.getId() );

        return couponProductResponse;
    }

    private Long entityCouponCouponId(CouponProduct couponProduct) {
        if ( couponProduct == null ) {
            return null;
        }
        Coupon coupon = couponProduct.getCoupon();
        if ( coupon == null ) {
            return null;
        }
        Long couponId = coupon.getCouponId();
        if ( couponId == null ) {
            return null;
        }
        return couponId;
    }

    private String entityCouponCode(CouponProduct couponProduct) {
        if ( couponProduct == null ) {
            return null;
        }
        Coupon coupon = couponProduct.getCoupon();
        if ( coupon == null ) {
            return null;
        }
        String code = coupon.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }

    private Long entityProductProductId(CouponProduct couponProduct) {
        if ( couponProduct == null ) {
            return null;
        }
        Product product = couponProduct.getProduct();
        if ( product == null ) {
            return null;
        }
        Long productId = product.getProductId();
        if ( productId == null ) {
            return null;
        }
        return productId;
    }

    private String entityProductName(CouponProduct couponProduct) {
        if ( couponProduct == null ) {
            return null;
        }
        Product product = couponProduct.getProduct();
        if ( product == null ) {
            return null;
        }
        String name = product.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
