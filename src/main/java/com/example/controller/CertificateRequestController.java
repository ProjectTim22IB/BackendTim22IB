package com.example.controller;

import com.example.dto.RequestCertificateDTO;
import com.example.model.CertificateRequest;
import com.example.repository.CertificateRepository;
import com.example.rest.Message;
import com.example.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificateRequest")
public class CertificateRequestController {
    private final ICertificateRequestService certificateRequestService;
    private final CertificateRepository certificateRepository;

    @Autowired
    public CertificateRequestController(ICertificateRequestService certificateRequestService, CertificateRepository certificateRepository){
        this.certificateRequestService = certificateRequestService;
        this.certificateRepository = certificateRepository;
    }

    @PostMapping(value = "/newRequest", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> requestCertificate(@RequestBody RequestCertificateDTO request){

        if (!this.certificateRepository.findBySerialNumber(request.getIssuerSerialNumber()).isPresent()){
            return new ResponseEntity<>(new Message("Can't create request because issuer certificate doesn't exists!"), HttpStatus.NOT_FOUND);
        }

        this.certificateRequestService.createRequest(request);
        return new ResponseEntity<>(new Message("Successfully created request for new certificate!"), HttpStatus.OK);

    }
}
