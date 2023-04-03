package com.example.model;

import com.example.enums.CertificateStatus;
import com.example.enums.CertificateType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Certificates")
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
