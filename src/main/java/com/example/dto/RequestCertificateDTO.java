package com.example.dto;

import com.example.enums.CertificateRequestStatus;
import com.example.enums.CertificateType;
import com.example.model.CertificateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestCertificateDTO {
    private CertificateType type;
    private String email;
    private String issuerSerialNumber;
    private LocalDate requestDate;

    public CertificateRequest parseToRequest(String issuerEmail) {
        return new CertificateRequest(type, email, issuerSerialNumber, requestDate, CertificateRequestStatus.PENDING, issuerEmail, "");
    }
}
