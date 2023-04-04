package com.example.service;

import com.example.dto.ApprovalOfRequestDTO;
import com.example.dto.RequestCertificateDTO;
import com.example.enums.CertificateRequestStatus;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CertificateService certificateService;

    @Autowired
    private CertificateRequestService(CertificateRequestRepository certificateRequestRepository, CertificateRepository certificateRepository, UserRepository userRepository, CertificateService certificateService){
        this.certificateRequestRepository = certificateRequestRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.certificateService = certificateService;
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
            this.certificateService.createNewCertificate(certificateRequest);
        }
        else if (issuerCertificate.getCertificateType() == CertificateType.ROOT){
            this.certificateService.createNewCertificate(certificateRequest);
        }
        else if (user.getRole() == Role.ADMIN) {
            this.certificateService.createNewCertificate(certificateRequest);
        }
    }

    @Override
    public List<CertificateRequest> getAllRequestsForUser(Long id) {
        User user = this.userRepository.getReferenceById(id);
        List<CertificateRequest> allRequests = this.certificateRequestRepository.findAll();
        List<CertificateRequest> allRequestsForUser = new ArrayList<CertificateRequest>();
        for(CertificateRequest c : allRequests){
            if(c.getEmail().equals(user.getEmail())){
                allRequestsForUser.add(c);
            }
        }
        return allRequestsForUser;
    }

    @Override
    public List<CertificateRequest> getAllRequestsForIssuer(Long id) {
        User user = this.userRepository.getReferenceById(id);
        List<CertificateRequest> allRequests = this.certificateRequestRepository.findAll();
        List<CertificateRequest> allRequestsForIssuer = new ArrayList<>();
        for(CertificateRequest c : allRequests){
            if(c.getIssuerEmail().equals(user.getEmail()) && c.getStatus() == CertificateRequestStatus.PENDING){
                allRequestsForIssuer.add(c);
            }
        }
        return allRequestsForIssuer;
    }

    @Override
    public void acceptRequest(Long requestId, ApprovalOfRequestDTO approvalOfRequest) {
        CertificateRequest certificateRequest = this.certificateRequestRepository.findById(requestId).get();
        if(!approvalOfRequest.isApproved()){
            certificateRequest.setStatus(CertificateRequestStatus.REJECTED);
            certificateRequest.setReasonOfRejection(approvalOfRequest.getReasonOfRejection());
            this.certificateRequestRepository.save(certificateRequest);
        } else {
            this.certificateService.createNewCertificate(certificateRequest);
        }
    }
}
