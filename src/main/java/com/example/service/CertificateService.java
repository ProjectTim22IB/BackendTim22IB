package com.example.service;

import com.example.enums.CertificateRequestStatus;
import com.example.enums.CertificateStatus;
import com.example.enums.CertificateType;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;
import com.example.repository.CertificateRepository;
import com.example.repository.CertificateRequestRepository;
import com.example.repository.UserRepository;
import com.example.service.interfaces.ICertificateService;
import com.example.service.interfaces.IUserService;
import com.example.utils.KeyStoreUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.cert.CertificateEncodingException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneId;
import java.util.*;

@Service
public class CertificateService implements ICertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificateRequestRepository certificateRequestRepository;
    private final UserRepository userRepository;
    private final IUserService userService;
    private final KeyStoreUtils keyStoreUtils;

    @Autowired
    private CertificateService(CertificateRepository certificateRepository, CertificateRequestRepository certificateRequestRepository, UserRepository userRepository, IUserService userService, KeyStoreUtils keyStoreUtils){
        this.certificateRepository = certificateRepository;
        this.certificateRequestRepository = certificateRequestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.keyStoreUtils = keyStoreUtils;
    }

    public List<Certificate> getAll() {
        return (List<Certificate>) this.certificateRepository.findAll();
    }

    @Override
    public Optional<Certificate> getCertificate(Long id) {
        return Optional.of(this.certificateRepository.getReferenceById(id));
    }

    @Override
    public Optional<Certificate> getCertificateBySerialNumber(String serialNumber) {
        return this.certificateRepository.findBySerialNumber(serialNumber);
    }

    @Override
    public boolean checkIfValid(String serialNumber) {
        boolean isValid = false;
        if(getCertificateBySerialNumber(serialNumber).get().getStatus() == CertificateStatus.VALID){
            isValid = true;
        }
        return isValid;
    }

    @Override
    public Certificate createNewCertificate(CertificateRequest request) {
        Random randomNum = new Random();
        int serialNumber = randomNum.nextInt(10000);
        while (this.certificateRepository.findBySerialNumber(String.valueOf(serialNumber)).isPresent()){
            serialNumber = randomNum.nextInt(10000);
        }
        Certificate certificate = request.parseToCertificate(String.valueOf(serialNumber));
        this.certificateRepository.save(certificate);

        request.setStatus(CertificateRequestStatus.APPROVED);
        this.certificateRequestRepository.save(request);

        return certificate;
    }

    @Override
    public void generateCertificate(CertificateRequest certificateRequest, Certificate certificate) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException, IOException, KeyStoreException {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");

        PrivateKey privateKey = generateKeyPair().getPrivate();
        ContentSigner contentSigner = builder.build(privateKey);

        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(this.userService.generateX500Name(this.userRepository.findByEmail(certificateRequest.getIssuerEmail()).get()), new BigInteger(certificate.getSerialNumber()), Date.from(certificate.getValidFrom().atZone(ZoneId.systemDefault()).toInstant()), Date.from(certificate.getValidTo().atZone(ZoneId.systemDefault()).toInstant()), this.userService.generateX500Name(this.userRepository.findByEmail(certificateRequest.getEmail()).get()), generateKeyPair().getPublic());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
        JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
        certificateConverter = certificateConverter.setProvider("BC");
        X509Certificate generatedCertificate = certificateConverter.getCertificate(certificateHolder);

        KeyStore keyStore;
        try {
            keyStore = this.keyStoreUtils.loadKeyStore();
        } catch (IOException e) {
            keyStore = this.keyStoreUtils.createKeyStore();
        }

        this.keyStoreUtils.saveCertificateToKeyStore(keyStore, certificate.getSerialNumber(), generatedCertificate, privateKey);

        this.keyStoreUtils.saveKeyStoreToFile(keyStore);
    }

    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

    @Override
    public void withdrawCertificate(String serialNumber) {
        Certificate certificate = getCertificateBySerialNumber(serialNumber).get();
        certificate.setStatus(CertificateStatus.NOTVALID);
        this.certificateRepository.save(certificate);
        List<Certificate> signedBy = findAllCertificatesSignedBy(certificate);
        for(Certificate c : signedBy){
            c.setStatus(CertificateStatus.NOTVALID);
            this.certificateRepository.save(c);
        }
    }

    @Override
    public List<Certificate> findAllCertificatesSignedBy(Certificate certificate) {
        List<Certificate> allCertificates = this.certificateRepository.findAll();
        List<Certificate> signedBy = new ArrayList<>();
        for(Certificate c : allCertificates){
            if(c.getIssuerSerialNumber() != null){
                if(c.getIssuerSerialNumber().equals(certificate.getSerialNumber())){
                    signedBy.add(c);
                }
            }
        }
        return signedBy;
    }


    @Override
    public void loadExistingCertificates() throws NoSuchProviderException, OperatorCreationException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        List<com.example.model.Certificate> allCertificates = getAll();
        for (com.example.model.Certificate c : allCertificates) {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");

            PrivateKey privateKey = generateKeyPair().getPrivate();
            ContentSigner contentSigner = builder.build(privateKey);

            String issuerEmail = null;
            com.example.model.Certificate issuerCertificate = null;
            X500Name x500Name = this.userService.generateX500Name(this.userRepository.findByEmail(c.getEmail()).get());

            if(c.getIssuerSerialNumber() != null){
                issuerCertificate = this.certificateRepository.findBySerialNumber(c.getIssuerSerialNumber()).get();
                issuerEmail = issuerCertificate.getEmail();
                x500Name = this.userService.generateX500Name(this.userRepository.findByEmail(issuerEmail).get());
            }


            X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(x500Name, new BigInteger(c.getSerialNumber()), Date.from(c.getValidFrom().atZone(ZoneId.systemDefault()).toInstant()), Date.from(c.getValidTo().atZone(ZoneId.systemDefault()).toInstant()), this.userService.generateX500Name(this.userRepository.findByEmail(c.getEmail()).get()), this.generateKeyPair().getPublic());

            if(c.getCertificateType() == CertificateType.ROOT){
                certificateBuilder.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints, true, new BasicConstraints(true));
            }

            X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
            JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
            certificateConverter = certificateConverter.setProvider("BC");
            X509Certificate generatedCertificate = certificateConverter.getCertificate(certificateHolder);

            KeyStore keyStore;
            try {
                keyStore = this.keyStoreUtils.loadKeyStore();
            } catch (IOException e) {
                keyStore = this.keyStoreUtils.createKeyStore();
            }

            keyStore.setCertificateEntry(c.getSerialNumber(), generatedCertificate);

            keyStore.setKeyEntry(c.getSerialNumber(), privateKey, "ibtim22".toCharArray(), new java.security.cert.Certificate[]{generatedCertificate});

            this.keyStoreUtils.saveKeyStoreToFile(keyStore);

        }

    }

}




