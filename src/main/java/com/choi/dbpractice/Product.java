package com.choi.dbpractice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "survey_date", nullable = false)
    private LocalDate surveyDate;

    @Column(name = "product_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @Column(name = "maker", nullable = false, length = 200)
    private String maker;

    @Column(name = "is_sale")
    private Boolean isSale;

    @Column(name = "is_one_plus_one")
    private Boolean isOnePlusOne;

    public Product(boolean isOnePlusOne, boolean isSale, String maker, String storeName, BigDecimal productPrice,
                   LocalDate surveyDate, String productName) {
        this.isOnePlusOne = isOnePlusOne;
        this.isSale = isSale;
        this.maker = maker;
        this.storeName = storeName;
        this.productPrice = productPrice;
        this.surveyDate = surveyDate;
        this.productName = productName;
    }

    protected Product() {}
}
