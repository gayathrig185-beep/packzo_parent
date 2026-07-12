package com.ecommerce.packzo.login.security;

import java.util.Collection;
import java.util.List;

import com.ecommerce.packzo.login.constants.Role;
import com.ecommerce.packzo.login.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



public class CustomUserDetails implements UserDetails {

    private final String userId;

    private final String firstName;

    private final String email;

    private final String password;

    private final Role role;

    private final boolean active;

    private final boolean emailVerified;

    private final boolean mobileVerified;

    public CustomUserDetails(User user) {

        this.userId = user.getUserId();

        this.firstName = user.getFirstName();

        this.email = user.getEmail();

        this.password = user.getPassword();

        this.role = user.getRole();

        this.active = Boolean.TRUE.equals(user.getActive());

        this.emailVerified = Boolean.TRUE.equals(user.getEmailVerified());

        this.mobileVerified = Boolean.TRUE.equals(user.getMobileVerified());
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Spring Security username
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Account Expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Account Locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    /**
     * Credentials Expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Enabled
     */
    @Override
    public boolean isEnabled() {
        return active;
    }

}