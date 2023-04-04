package com.example.mapper;

import com.example.dto.RequestCertificateDTO;
import com.example.model.CertificateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CertificateMapper {

    CertificateMapper MAPPER = Mappers.getMapper(CertificateMapper.class);

    CertificateRequest requestDtoToRequest(RequestCertificateDTO request);
}
