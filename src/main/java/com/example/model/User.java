package com.example.model;

import com.example.enums.AutentificationType;
import com.example.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String phoneNumber;

    private String email;

    private String password;

    private boolean autentificated;

    private String resetPasswordToken;

    private LocalDateTime resetPasswordTokenExpiration;

    private LocalDateTime passwordExpiration;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ElementCollection
    private List<String> oldPasswords = new ArrayList<>();

    private LocalDateTime lastPasswordResetDate;

    private AutentificationType autentificationType;

    private boolean twoFactorAuth;

    private String twoFactorAuthToken;

    private LocalDateTime twoFactorAuthTokenExpiration;

    private LocalDateTime twoFactorAuthTokenSession;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAutentificated() {
        return autentificated;
    }

    public void setAutentificated(boolean autentificated) {
        this.autentificated = autentificated;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordTokenExpiration() {
        return resetPasswordTokenExpiration;
    }

    public void setResetPasswordTokenExpiration(LocalDateTime resetPasswordTokenExpiration) {
        this.resetPasswordTokenExpiration = resetPasswordTokenExpiration;
    }

    public LocalDateTime getPasswordExpiration() {
        return passwordExpiration;
    }

    public void setPasswordExpiration(LocalDateTime passwordExpiration) {
        this.passwordExpiration = passwordExpiration;
    }

    public List<String> getOldPasswords() {
        return oldPasswords;
    }

    public void setOldPasswords(List<String> oldPasswords) {
        this.oldPasswords = oldPasswords;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(LocalDateTime lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public AutentificationType getAutentificationType() {
        return autentificationType;
    }

    public void setAutentificationType(AutentificationType autentificationType) {
        this.autentificationType = autentificationType;
    }

    public boolean isTwoFactorAuth() {
        return twoFactorAuth;
    }

    public void setTwoFactorAuth(boolean twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

    public String getTwoFactorAuthToken() {
        return twoFactorAuthToken;
    }

    public void setTwoFactorAuthToken(String twoFactorAuthToken) {
        this.twoFactorAuthToken = twoFactorAuthToken;
    }

    public LocalDateTime getTwoFactorAuthTokenExpiration() {
        return twoFactorAuthTokenExpiration;
    }

    public void setTwoFactorAuthTokenExpiration(LocalDateTime twoFactorAuthTokenExpiration) {
        this.twoFactorAuthTokenExpiration = twoFactorAuthTokenExpiration;
    }

    public LocalDateTime getTwoFactorAuthTokenSession() {
        return twoFactorAuthTokenSession;
    }

    public void setTwoFactorAuthTokenSession(LocalDateTime twoFactorAuthTokenSession) {
        this.twoFactorAuthTokenSession = twoFactorAuthTokenSession;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.getRole().toString()));
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
