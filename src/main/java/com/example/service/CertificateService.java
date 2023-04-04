package com.example.service;

import com.example.dto.RequestCertificateDTO;
import com.example.enums.CertificateRequestStatus;
import com.example.enums.CertificateStatus;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;
import com.example.repository.CertificateRepository;
import com.example.repository.CertificateRequestRepository;
import com.example.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CertificateService implements ICertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificateRequestRepository certificateRequestRepository;

    @Autowired
    private CertificateService(CertificateRepository certificateRepository, CertificateRequestRepository certificateRequestRepository){
        this.certificateRepository = certificateRepository;
        this.certificateRequestRepository = certificateRequestRepository;
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
        boolean isValid = false;
        if(getCertificate(id).get().getStatus() == CertificateStatus.VALID){
            isValid = true;
        }
        return isValid;
    }

    @Override
    public void createNewCertificate(CertificateRequest request) {
        Random randomNum = new Random();
        int serialNumber = randomNum.nextInt(10000);
        while (this.certificateRepository.findBySerialNumber(String.valueOf(serialNumber)).isPresent()){
            serialNumber = randomNum.nextInt(10000);
        }
        Certificate certificate = request.parseToCertificate(String.valueOf(serialNumber));
        this.certificateRepository.save(certificate);

        request.setStatus(CertificateRequestStatus.APPROVED);
        this.certificateRequestRepository.save(request);
    }
}
