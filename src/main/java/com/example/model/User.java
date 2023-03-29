package com.example.model;

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
@Table(name = "Userr")
public class User implements UserDetails {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Surname")
    private String surname;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Email")
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "ConfromationToken")
    private String conformationToken;

    @Column(name = "Autentificated")
    private boolean autentificated;

    @Column(name = "ResetPasswordToken")
    private String resetPasswordToken;

    @Column(name = "ResetPasswordTokenExpiration")
    private LocalDateTime resetPasswordTokenExpiration;

    @Column(name = "PasswordExpiration")
    private LocalDateTime passwordExpiration;

    @Column(name = "Role")
    @Enumerated(EnumType.STRING)
    private Role role;

//    @Column(name = "OldPasswords")
//    @ElementCollection
//    private List<String> oldPasswords;

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

    public String getConformationToken() {
        return conformationToken;
    }

    public void setConformationToken(String conformationToken) {
        this.conformationToken = conformationToken;
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

//    public List<String> getOldPasswords() {
//        return oldPasswords;
//    }
//
//    public void setOldPasswords(List<String> oldPasswords) {
//        this.oldPasswords = oldPasswords;
//    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
