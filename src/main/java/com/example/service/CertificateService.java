package com.example.service;

import com.example.dto.RequestCertificateDTO;
import com.example.enums.CertificateRequestStatus;
import com.example.enums.CertificateStatus;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;
import com.example.repository.CertificateRepository;
import com.example.repository.CertificateRequestRepository;
import com.example.repository.UserRepository;
import com.example.service.interfaces.ICertificateService;
import com.example.service.interfaces.IUserService;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CertificateService implements ICertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificateRequestRepository certificateRequestRepository;
    private final UserRepository userRepository;
    private final IUserService userService;

    @Autowired
    private CertificateService(CertificateRepository certificateRepository, CertificateRequestRepository certificateRequestRepository, UserRepository userRepository, IUserService userService){
        this.certificateRepository = certificateRepository;
        this.certificateRequestRepository = certificateRequestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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

    @Override
    public void generateCertificate(CertificateRequest certificateRequest, Certificate certificate) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");

        ContentSigner contentSigner = builder.build(generateKeyPair().getPrivate());

        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(this.userService.generateX500Name(this.userRepository.findByEmail(certificateRequest.getIssuerEmail()).get()), new BigInteger(certificate.getSerialNumber()), Date.from(certificate.getValidFrom().atZone(ZoneId.systemDefault()).toInstant()), Date.from(certificate.getValidTo().atZone(ZoneId.systemDefault()).toInstant()), this.userService.generateX500Name(this.userRepository.findByEmail(certificateRequest.getEmail()).get()), generateKeyPair().getPublic());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
        JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
        certificateConverter = certificateConverter.setProvider("BC");
        X509Certificate generatedCertificate = certificateConverter.getCertificate(certificateHolder);
    }

    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }


}
