package com.example.EcommerceBackendProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorResponseDTO {

    private int status;

    private String error;

    private String message;

    private String path;

    LocalDateTime timestamp;
}
