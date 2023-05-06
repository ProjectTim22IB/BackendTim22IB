package com.example.controller;

import com.example.dto.RequestCertificateDTO;
import com.example.model.Certificate;
import com.example.repository.CertificateRepository;
import com.example.rest.Message;
import com.example.service.interfaces.ICertificateRequestService;
import com.example.service.interfaces.ICertificateService;
import com.example.service.interfaces.IUserService;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final ICertificateService certificateService;
    private final CertificateRepository certificateRepository;

    @Autowired
    public CertificateController(ICertificateService certificateService, CertificateRepository certificateRepository) {
        this.certificateService = certificateService;
        this.certificateRepository = certificateRepository;
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

}
