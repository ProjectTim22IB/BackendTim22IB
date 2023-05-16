package com.example.controller;

import com.example.dto.CertificateWithdrawalDTO;
import com.example.dto.RequestCertificateDTO;
import com.example.enums.Role;
import com.example.model.Certificate;
import com.example.repository.CertificateRepository;
import com.example.repository.UserRepository;
import com.example.rest.Message;
import com.example.service.interfaces.ICertificateRequestService;
import com.example.service.interfaces.ICertificateService;
import com.example.service.interfaces.IUserService;
import com.example.utils.KeyStoreUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Objects;


@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final ICertificateService certificateService;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final KeyStoreUtils keyStoreUtils;

    @Autowired
    public CertificateController(ICertificateService certificateService, CertificateRepository certificateRepository, UserRepository userRepository, KeyStoreUtils keyStoreUtils) {
        this.certificateService = certificateService;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.keyStoreUtils = keyStoreUtils;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> getAllCertificates() {
        return new ResponseEntity<>(this.certificateService.getAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/valid/{serialNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> checkIfValid(@PathVariable("serialNumber") String serialNumber){
        if(!this.certificateRepository.findBySerialNumber(serialNumber).isPresent()){
            return new ResponseEntity<>(new Message("Certificate does not exist!"), HttpStatus.NOT_FOUND);
        }
        boolean isValid = this.certificateService.checkIfValid(serialNumber);
        if (isValid) {
            return new ResponseEntity<>(new Message("Certificate is valid!"), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new Message("Certificate is not valid!"), HttpStatus.OK);
        }
    }

    @PutMapping(value = "withdraw/{serialNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> withdrawCertificate(@PathVariable("serialNumber") String serialNumber, @RequestBody CertificateWithdrawalDTO withdrawalDTO){
        if(!this.certificateRepository.findBySerialNumber(serialNumber).isPresent()){
            return new ResponseEntity<>(new Message("Certificate does not exist!"), HttpStatus.NOT_FOUND);
        }
        if(!this.certificateRepository.findBySerialNumber(serialNumber).get().getEmail().equals(withdrawalDTO.getEmail()) && this.userRepository.findByEmail(withdrawalDTO.getEmail()).get().getRole() != Role.ADMIN){
            return new ResponseEntity<>(new Message("Can't withdraw certificate which you don't own!"), HttpStatus.BAD_REQUEST);
        }
        this.certificateService.withdrawCertificate(serialNumber);
        return new ResponseEntity<>(new Message("Successfully withdrawn certificate!"), HttpStatus.OK);
    }

    @GetMapping("/download/{serialNumber}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> downloadCertificate(@PathVariable("serialNumber") String serialNumber) {
        if(!this.certificateRepository.findBySerialNumber(serialNumber).isPresent()){
            return new ResponseEntity<>(new Message("Certificate with this serial number does not exist!"), HttpStatus.NOT_FOUND);
        }
        try {
            KeyStore keyStore = this.keyStoreUtils.loadKeyStore();
//            this.certificateService.loadExistingCertificates();

            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(serialNumber);

            byte[] certificateData = certificate.getEncoded();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", serialNumber + ".cer");

            return new ResponseEntity<>(certificateData, headers, HttpStatus.OK);
        } catch (KeyStoreException | IOException | CertificateException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/valid")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> checkCertificateValidity(@RequestParam("file") MultipartFile file) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) factory.generateCertificate(file.getInputStream());

            String serialNumber = cert.getSerialNumber().toString(16).toUpperCase();

            boolean isValid = this.certificateService.checkIfValid(serialNumber);
            if (isValid) {
                return new ResponseEntity<>(new Message("Certificate is valid!"), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new Message("Certificate is not valid!"), HttpStatus.OK);
            }
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Message("Failed to parse certificate."), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addtoKeystore")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> addtoKeystore() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
        this.certificateService.loadExistingCertificates();
        return new ResponseEntity<>(new Message("Successfully added root to keystore!"), HttpStatus.OK);
    }

}
