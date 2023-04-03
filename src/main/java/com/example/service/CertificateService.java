package com.example.service;

import com.example.model.Certificate;
import com.example.repository.CertificateRepository;
import com.example.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService implements ICertificateService {

    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateService(CertificateRepository certificateRepository){
        this.certificateRepository = certificateRepository;
    }

    public List<Certificate> getAll() {
        return (List<Certificate>) this.certificateRepository.findAll();
    }
}
