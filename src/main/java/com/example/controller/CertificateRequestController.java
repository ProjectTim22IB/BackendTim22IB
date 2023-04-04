package com.example.controller;

import com.example.dto.RequestCertificateDTO;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;
import com.example.repository.CertificateRepository;
import com.example.repository.UserRepository;
import com.example.rest.Message;
import com.example.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/certificateRequest")
public class CertificateRequestController {
    private final ICertificateRequestService certificateRequestService;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    @Autowired
    public CertificateRequestController(ICertificateRequestService certificateRequestService, CertificateRepository certificateRepository, UserRepository userRepository){
        this.certificateRequestService = certificateRequestService;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
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

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> getAllRequests(@PathVariable("userId") Long id){
        if(!this.userRepository.findById(id).isPresent()){
            return new ResponseEntity<>(new Message("User with this id doesn't exist!"), HttpStatus.NOT_FOUND);
        }
        List<CertificateRequest> allRequests = this.certificateRequestService.getAllRequestsForUser(id);
        if (allRequests.size() == 0){
            return new ResponseEntity<>(new Message("This user doesn't have any requests for certificates!"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }
}
