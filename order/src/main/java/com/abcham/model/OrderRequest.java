package com.abcham.model;

import java.math.BigDecimal;

public record OrderRequest(String customerName, BigDecimal totalAmount) {

}
