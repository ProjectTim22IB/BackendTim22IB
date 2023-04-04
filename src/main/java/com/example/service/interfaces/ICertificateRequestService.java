package com.example.service.interfaces;

import com.example.dto.ApprovalOfRequestDTO;
import com.example.dto.RequestCertificateDTO;
import com.example.model.CertificateRequest;

import java.util.List;

public interface ICertificateRequestService {
    void add(CertificateRequest certificateRequest);
    void createRequest(RequestCertificateDTO request);

    List<CertificateRequest> getAllRequestsForUser(Long id);

    List<CertificateRequest> getAllRequestsForIssuer(Long id);

    void acceptRequest(Long requestId, ApprovalOfRequestDTO approvalOfRequest);
}
