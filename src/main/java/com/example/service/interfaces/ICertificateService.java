package com.example.service.interfaces;

import com.example.dto.RequestCertificateDTO;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;
import org.bouncycastle.operator.OperatorCreationException;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Optional;

public interface ICertificateService {
    List<Certificate> getAll();

    Optional<Certificate> getCertificate(Long id);

    boolean checkIfValid(Long id);

    void createNewCertificate(CertificateRequest request);

    void generateCertificate(CertificateRequest certificateRequest, Certificate certificate) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException;

    KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException;
}
