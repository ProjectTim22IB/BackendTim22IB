package com.example.service.interfaces;

import com.example.dto.RequestCertificateDTO;
import com.example.model.CertificateRequest;

import java.util.List;

public interface ICertificateRequestService {
    void add(CertificateRequest certificateRequest);
    void createRequest(RequestCertificateDTO request);

    List<CertificateRequest> getAllRequestsForUser(Long id);
}
