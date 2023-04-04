package com.example.service;

import com.example.dto.RequestCertificateDTO;
import com.example.enums.CertificateStatus;
import com.example.model.Certificate;
import com.example.repository.CertificateRepository;
import com.example.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateService implements ICertificateService {

    private final CertificateRepository certificateRepository;

    @Autowired
    private CertificateService(CertificateRepository certificateRepository){
        this.certificateRepository = certificateRepository;
    }

    public List<Certificate> getAll() {
        return (List<Certificate>) this.certificateRepository.findAll();
    }

    @Override
    public Optional<Certificate> getCertificate(Long id) {
        return Optional.of(this.certificateRepository.getReferenceById(id));
    }

    @Override
    public boolean checkIfValid(Long id) {
        return getCertificate(id).get().getStatus() != CertificateStatus.VALID;
    }
}
