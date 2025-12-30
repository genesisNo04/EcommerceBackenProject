package com.example.EcommerceBackendProject.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponseDTO {

    private Long productId;

    private Long userId;

    private String username;

    private String productName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}
