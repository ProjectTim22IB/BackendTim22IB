package com.example.controller;

import com.example.model.Certificate;
import com.example.rest.Message;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final ICertificateService certificateService;

    @Autowired
    public CertificateController(ICertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> getAllCertificates() {
        List<Certificate> certificates = this.certificateService.getAll();
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

    @GetMapping(value = "/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> checkIfValid(@PathVariable("id") Long id){
//        if(!StringUtils.isNumeric(id)){
//            return new ResponseEntity<>(new Message("Id is not numeric"), HttpStatus.NOT_FOUND);
//        }
        if(!this.certificateService.getCertificate(id).isPresent()){
            return new ResponseEntity<>(new Message("Certificate does not exist"), HttpStatus.NOT_FOUND);
        }
        boolean isValid = this.certificateService.checkIfValid(id);

        if (isValid) {
            return new ResponseEntity<>(new Message("Certificate is valid"), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new Message("Certificate is not valid"), HttpStatus.OK);
        }


    }
}
