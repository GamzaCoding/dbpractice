package com.choi.dbpractice;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<Product> findByProductName(String productName) {
        return productRepository.findByProductName(productName);
    }
}
