package com.example.controller;

import com.example.dto.ApprovalOfRequestDTO;
import com.example.dto.RequestCertificateDTO;
import com.example.enums.CertificateRequestStatus;
import com.example.enums.CertificateType;
import com.example.model.Certificate;
import com.example.model.CertificateRequest;
import com.example.repository.CertificateRepository;
import com.example.repository.CertificateRequestRepository;
import com.example.repository.UserRepository;
import com.example.rest.Message;
import com.example.service.interfaces.ICertificateRequestService;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/certificateRequest")
public class CertificateRequestController {
    private final ICertificateRequestService certificateRequestService;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CertificateRequestRepository certificateRequestRepository;

    @Autowired
    public CertificateRequestController(ICertificateRequestService certificateRequestService, CertificateRepository certificateRepository, UserRepository userRepository, CertificateRequestRepository certificateRequestRepository){
        this.certificateRequestService = certificateRequestService;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.certificateRequestRepository = certificateRequestRepository;
    }

    @PostMapping(value = "/newRequest", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> requestCertificate(@RequestBody RequestCertificateDTO request){

        if (!this.certificateRepository.findBySerialNumber(request.getIssuerSerialNumber()).isPresent()){
            return new ResponseEntity<>(new Message("Can't create request because issuer's certificate doesn't exists!"), HttpStatus.NOT_FOUND);
        }

        if(this.certificateRepository.findBySerialNumber(request.getIssuerSerialNumber()).get().getCertificateType() == CertificateType.END){
            return new ResponseEntity<>(new Message("Can't create request because issuer's certificate type is end!"), HttpStatus.NOT_FOUND);
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

    @GetMapping(value = "/issuer/{issuerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> getAllRequestsForIssuer(@PathVariable("issuerId") Long id){
        if(!this.userRepository.findById(id).isPresent()){
            return new ResponseEntity<>(new Message("User with this id doesn't exist!"), HttpStatus.NOT_FOUND);
        }
        List<CertificateRequest> allRequests = this.certificateRequestService.getAllRequestsForIssuer(id);
        if (allRequests.size() == 0){
            return new ResponseEntity<>(new Message("This user doesn't have any requests for certificates!"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @GetMapping(value = "/allRequests", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> getAllRequests(){
        List<CertificateRequest> allRequests = this.certificateRequestRepository.findAll();
        if(allRequests.size() == 0){
            return new ResponseEntity<>(new Message("There are no certificate requests!"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allRequests, HttpStatus.OK);
    }

    @PostMapping(value = "/acceptance/{requestId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> acceptanceOfRequest(@PathVariable("requestId") Long requestId, @RequestBody ApprovalOfRequestDTO approvalOfRequest) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException, OperatorCreationException {
        if(!this.certificateRequestRepository.findById(requestId).isPresent()){
            return new ResponseEntity<>(new Message("Request with this id doesn't exist!"), HttpStatus.NOT_FOUND);
        }
        CertificateRequest certificateRequest = this.certificateRequestRepository.findById(requestId).get();
        if(!certificateRequest.getIssuerEmail().equals(approvalOfRequest.getIssuerEmail())){
            return new ResponseEntity<>(new Message("Can't accept/reject certificate request for that you're not issuer!"), HttpStatus.NOT_FOUND);
        }
        if(certificateRequest.getStatus() == CertificateRequestStatus.APPROVED || certificateRequest.getStatus() == CertificateRequestStatus.REJECTED){
            return new ResponseEntity<>(new Message("Can't accept/reject request that is already approved/rejected!"), HttpStatus.BAD_REQUEST);
        }
        this.certificateRequestService.acceptRequest(requestId, approvalOfRequest);
        if(!approvalOfRequest.isApproved()){
            return new ResponseEntity<>(new Message("Successfully rejected the request!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Message("Successfully accepted the request and create new certificate!"), HttpStatus.OK);
    }
}
