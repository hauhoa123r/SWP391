package org.project.converter;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.project.config.ModelMapperConfig;
import org.project.entity.UserEntity;
import org.project.model.response.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@NoArgsConstructor
public class toConverterLogin {
    @Autowired
    private ModelMapperConfig modelMapper;

    public UserLoginResponse toConverterUserLoginResponse(UserEntity userEntity) {
        UserLoginResponse userLoginResponse = modelMapper.mapper().map(userEntity, UserLoginResponse.class);
        return userLoginResponse;
    }
}
