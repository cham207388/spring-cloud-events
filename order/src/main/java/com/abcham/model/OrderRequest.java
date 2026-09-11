package com.abcham.model;

import java.math.BigDecimal;

public record OrderRequest(Long id, String customerName, BigDecimal totalAmount) {

}
