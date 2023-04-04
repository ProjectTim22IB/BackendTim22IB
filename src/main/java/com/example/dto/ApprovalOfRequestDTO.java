package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApprovalOfRequestDTO {
    private boolean approved;
    private String reasonOfRejection;
    private String issuerEmail;
}
