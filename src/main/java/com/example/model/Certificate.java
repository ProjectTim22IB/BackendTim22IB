package com.example.model;

import com.example.enums.CertificateStatus;
import com.example.enums.CertificateType;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Certificate {

    @Id
    private Long id;
    private String serialNumber;
    private String signatureAlgorithm;
    private String issuer;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private CertificateStatus status;
    private CertificateType certificateType;
    private String username;
}
