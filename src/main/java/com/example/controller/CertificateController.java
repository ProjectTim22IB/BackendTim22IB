package com.example.controller;

import com.example.model.Certificate;
import com.example.service.interfaces.ICertificateService;
import com.example.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getAllPassengers(Pageable page) {
        List<Certificate> certificates = this.certificateService.getAll();

        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }
}
