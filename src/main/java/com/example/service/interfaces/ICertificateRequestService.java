package com.example.service.interfaces;

import com.example.dto.ApprovalOfRequestDTO;
import com.example.dto.RequestCertificateDTO;
import com.example.model.CertificateRequest;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;

public interface ICertificateRequestService {
    void add(CertificateRequest certificateRequest);
    void createRequest(RequestCertificateDTO request);

    List<CertificateRequest> getAllRequestsForUser(Long id);

    List<CertificateRequest> getAllRequestsForIssuer(Long id);

    void acceptRequest(Long requestId, ApprovalOfRequestDTO approvalOfRequest) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException, OperatorCreationException;
}
