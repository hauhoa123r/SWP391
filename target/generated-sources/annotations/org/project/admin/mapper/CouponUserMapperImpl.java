package org.project.admin.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.project.admin.dto.request.CouponUserRequest;
import org.project.admin.dto.response.CouponUserResponse;
import org.project.admin.entity.Coupon;
import org.project.admin.entity.CouponUser;
import org.project.admin.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T22:32:22+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class CouponUserMapperImpl implements CouponUserMapper {

    @Override
    public CouponUserResponse toResponse(CouponUser entity) {
        if ( entity == null ) {
            return null;
        }

        CouponUserResponse couponUserResponse = new CouponUserResponse();

        couponUserResponse.setCouponId( entityCouponCouponId( entity ) );
        couponUserResponse.setCouponCode( entityCouponCode( entity ) );
        couponUserResponse.setUserId( entityUserUserId( entity ) );
        couponUserResponse.setUserEmail( entityUserEmail( entity ) );
        couponUserResponse.setUserPhoneNumber( entityUserPhoneNumber( entity ) );
        couponUserResponse.setId( entity.getId() );
        couponUserResponse.setUsedCount( entity.getUsedCount() );

        return couponUserResponse;
    }

    @Override
    public List<CouponUserResponse> toResponseList(List<CouponUser> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CouponUserResponse> list = new ArrayList<CouponUserResponse>( entities.size() );
        for ( CouponUser couponUser : entities ) {
            list.add( toResponse( couponUser ) );
        }

        return list;
    }

    @Override
    public CouponUser toEntity(CouponUserRequest request) {
        if ( request == null ) {
            return null;
        }

        CouponUser couponUser = new CouponUser();

        return couponUser;
    }

    private Long entityCouponCouponId(CouponUser couponUser) {
        if ( couponUser == null ) {
            return null;
        }
        Coupon coupon = couponUser.getCoupon();
        if ( coupon == null ) {
            return null;
        }
        Long couponId = coupon.getCouponId();
        if ( couponId == null ) {
            return null;
        }
        return couponId;
    }

    private String entityCouponCode(CouponUser couponUser) {
        if ( couponUser == null ) {
            return null;
        }
        Coupon coupon = couponUser.getCoupon();
        if ( coupon == null ) {
            return null;
        }
        String code = coupon.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }

    private Long entityUserUserId(CouponUser couponUser) {
        if ( couponUser == null ) {
            return null;
        }
        User user = couponUser.getUser();
        if ( user == null ) {
            return null;
        }
        Long userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    private String entityUserEmail(CouponUser couponUser) {
        if ( couponUser == null ) {
            return null;
        }
        User user = couponUser.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String entityUserPhoneNumber(CouponUser couponUser) {
        if ( couponUser == null ) {
            return null;
        }
        User user = couponUser.getUser();
        if ( user == null ) {
            return null;
        }
        String phoneNumber = user.getPhoneNumber();
        if ( phoneNumber == null ) {
            return null;
        }
        return phoneNumber;
    }
}
