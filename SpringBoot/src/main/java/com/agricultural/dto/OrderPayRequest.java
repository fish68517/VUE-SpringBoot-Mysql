package com.agricultural.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderPayRequest {

    private String paymentMethod;
}