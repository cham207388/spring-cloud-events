package com.abcham.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private Long id;
    private String customerName;
    private BigDecimal totalAmount;
    private String status;

}
