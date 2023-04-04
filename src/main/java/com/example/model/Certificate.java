package com.example.model;

import com.example.enums.CertificateStatus;
import com.example.enums.CertificateType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String serialNumber;
    private String issuerSerialNumber;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @Enumerated(EnumType.STRING)
    private CertificateStatus status;

    @Enumerated(EnumType.STRING)
    private CertificateType certificateType;
    private String email;

    public Certificate(String serialNumber, String issuerSerialNumber, LocalDateTime validFrom, LocalDateTime validTo, CertificateStatus status, CertificateType certificateType, String email) {
        this.serialNumber = serialNumber;
        this.issuerSerialNumber = issuerSerialNumber;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
        this.certificateType = certificateType;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIssuerSerialNumber() { return issuerSerialNumber; }

    public void setIssuerSerialNumber(String issuerSerialNumber) { this.issuerSerialNumber = issuerSerialNumber; }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
