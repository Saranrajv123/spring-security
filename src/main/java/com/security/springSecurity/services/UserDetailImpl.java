package com.security.springSecurity.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.security.springSecurity.models.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailImpl implements UserDetails {

    private UUID id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;

    Collection<? extends GrantedAuthority> authorities;

//    public UserDetailImpl(UUID id, String username, String password, String email, List<SimpleGrantedAuthority> authorities) {
//        this.id = id;
//    }

    public static UserDetailImpl build(User user) {

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map((role) -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailImpl(
                user.getId(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail(),
                authorities
        );


    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
