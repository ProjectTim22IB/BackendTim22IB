package com.example.service.interfaces;

import com.example.dto.RequestCertificateDTO;
import com.example.model.CertificateRequest;

public interface ICertificateRequestService {
    void add(CertificateRequest certificateRequest);
    void createRequest(RequestCertificateDTO request);
}
