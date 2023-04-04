package com.example.service.interfaces;

import com.example.dto.RequestCertificateDTO;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;

import java.util.List;
import java.util.Optional;

public interface ICertificateService {
    List<Certificate> getAll();

    Optional<Certificate> getCertificate(Long id);

    boolean checkIfValid(Long id);

    void createNewCertificate(CertificateRequest request);
}