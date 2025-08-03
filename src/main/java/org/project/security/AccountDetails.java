package org.project.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.project.entity.UserEntity;
import org.project.enums.StaffRole;
import org.project.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class AccountDetails implements UserDetails, OidcUser {
    private final UserEntity userEntity;
    private final Map<String, Object> attributes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userEntity.getUserRole() == UserRole.STAFF &&
                userEntity.getStaffEntity() != null &&
                userEntity.getStaffEntity().getStaffRole() != null) {

            return List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getStaffEntity().getStaffRole().name()));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getUserRole().name()));
    }

    @Override
    public String getPassword() {
        return this.userEntity.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return this.userEntity.getEmail();
    }

    @Override
    public String getName() {
        return this.userEntity.getEmail();
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return new OidcUserInfo(attributes);
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }
}
