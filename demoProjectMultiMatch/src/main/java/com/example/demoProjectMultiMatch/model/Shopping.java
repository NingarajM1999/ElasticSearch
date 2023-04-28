package com.example.demoProjectMultiMatch.model;

import lombok.Data;

import java.util.List;

@Data
public class Shopping {

    private String productId;
    private String productName;
    private String productDescription;
    private List<String> productCompanyName;
    private List<String> productPrice;
    private List<String> productColor;
}
