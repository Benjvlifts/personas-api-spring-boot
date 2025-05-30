package com.gateway.jwt.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String user;
    private String pass;
    private int estado;
    private int idPersona;
}
