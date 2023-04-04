package com.example.service;

import com.example.dto.RequestCertificateDTO;
import com.example.enums.CertificateType;
import com.example.enums.Role;
import com.example.mapper.CertificateMapper;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;
import com.example.model.User;
import com.example.repository.CertificateRepository;
import com.example.repository.CertificateRequestRepository;
import com.example.repository.UserRepository;
import com.example.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    @Autowired
    private CertificateRequestService(CertificateRequestRepository certificateRequestRepository, CertificateRepository certificateRepository, UserRepository userRepository){
        this.certificateRequestRepository = certificateRequestRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void add(CertificateRequest certificateRequest) {
        this.certificateRequestRepository.save(certificateRequest);
    }

    @Override
    public void createRequest(RequestCertificateDTO request) {
        Certificate issuerCertificate = this.certificateRepository.findBySerialNumber(request.getIssuerSerialNumber()).get();
        String issuerEmail = issuerCertificate.getEmail();

        CertificateRequest certificateRequest = request.parseToRequest(issuerEmail);
        this.add(certificateRequest);

        User user = this.userRepository.findByEmail(request.getEmail()).get();

        if (request.getEmail().equals(issuerEmail)){
            return;
        }
        else if (issuerCertificate.getCertificateType() == CertificateType.ROOT){
            return;
        }
        else if (user.getRole() == Role.ADMIN) {
            return;
        }
    }
}
