package org.project.admin.mapper;

import javax.annotation.processing.Generated;
import org.project.admin.dto.request.UserRequest;
import org.project.admin.dto.response.UserResponse;
import org.project.admin.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-24T11:18:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRequest dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setUserRole( dto.getUserRole() );
        user.setPhoneNumber( dto.getPhoneNumber() );
        user.setEmail( dto.getEmail() );
        user.setUserStatus( dto.getUserStatus() );
        user.setTwoFactorEnabled( dto.getTwoFactorEnabled() );

        return user;
    }

    @Override
    public UserResponse toResponse(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setUserId( entity.getUserId() );
        userResponse.setUserRole( entity.getUserRole() );
        userResponse.setPhoneNumber( entity.getPhoneNumber() );
        userResponse.setEmail( entity.getEmail() );
        userResponse.setUserStatus( entity.getUserStatus() );
        userResponse.setIsVerified( entity.getIsVerified() );
        userResponse.setTwoFactorEnabled( entity.getTwoFactorEnabled() );

        return userResponse;
    }

    @Override
    public void updateEntityFromDto(UserRequest dto, User entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getUserRole() != null ) {
            entity.setUserRole( dto.getUserRole() );
        }
        if ( dto.getPhoneNumber() != null ) {
            entity.setPhoneNumber( dto.getPhoneNumber() );
        }
        if ( dto.getEmail() != null ) {
            entity.setEmail( dto.getEmail() );
        }
        if ( dto.getUserStatus() != null ) {
            entity.setUserStatus( dto.getUserStatus() );
        }
        if ( dto.getTwoFactorEnabled() != null ) {
            entity.setTwoFactorEnabled( dto.getTwoFactorEnabled() );
        }
    }
}
