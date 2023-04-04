package com.example.model;

import com.example.enums.CertificateRequestStatus;
import com.example.enums.CertificateType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certificate_requests")
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
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
}
