package com.example.model;

import com.example.enums.CertificateRequestStatus;
import com.example.enums.CertificateStatus;
import com.example.enums.CertificateType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certificate_requests")
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CertificateType type;
    private String email;
    private String issuerSerialNumber;
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    private CertificateRequestStatus status;
    private String issuerEmail;
    private String reasonOfRejection;

    public CertificateRequest(CertificateType type, String email, String issuerSerialNumber, LocalDate requestDate, CertificateRequestStatus status, String issuerEmail, String reasonOfRejection) {
        this.type = type;
        this.email = email;
        this.issuerSerialNumber = issuerSerialNumber;
        this.requestDate = requestDate;
        this.status = status;
        this.issuerEmail = issuerEmail;
        this.reasonOfRejection = reasonOfRejection;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertificateType getType() {
        return type;
    }

    public void setType(CertificateType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIssuerSerialNumber() {
        return issuerSerialNumber;
    }

    public void setIssuerSerialNumber(String issuerSerialNumber) {
        this.issuerSerialNumber = issuerSerialNumber;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public CertificateRequestStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateRequestStatus status) {
        this.status = status;
    }

    public String getIssuerEmail() {
        return issuerEmail;
    }

    public void setIssuerEmail(String issuerEmail) {
        this.issuerEmail = issuerEmail;
    }

    public String getReasonOfRejection() {
        return reasonOfRejection;
    }

    public void setReasonOfRejection(String reasonOfRejection) {
        this.reasonOfRejection = reasonOfRejection;
    }

    public Certificate parseToCertificate(String serialNumber) {
        Certificate certificate = null;
        if(type == CertificateType.ROOT){
            certificate = new Certificate(serialNumber, issuerSerialNumber, LocalDateTime.now(), LocalDateTime.now().plusYears(1), CertificateStatus.VALID, type, email);
        } else if (type == CertificateType.INTERMEDIATE) {
            certificate = new Certificate(serialNumber, issuerSerialNumber, LocalDateTime.now(), LocalDateTime.now().plusMonths(6), CertificateStatus.VALID, type, email);
        } else {
            certificate = new Certificate(serialNumber, issuerSerialNumber, LocalDateTime.now(), LocalDateTime.now().plusMonths(3), CertificateStatus.VALID, type, email);
        }
        return certificate;
    }
}
