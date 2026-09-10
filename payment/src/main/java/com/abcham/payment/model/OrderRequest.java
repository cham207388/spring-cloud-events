package com.abcham.payment.model;

public record OrderRequest(Long id, String customerName, Double totalAmount) {

}

