package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.LoginRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
