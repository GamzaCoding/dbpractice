package com.choi.dbpractice;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/home")
public class homeController {

    public final ProductService productService;

    public homeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String home(@RequestParam(required = false) String productName, Model model) {
        model.addAttribute("productName", productName);

        if (productName != null && !productName.isBlank()) {
            List<Product> products = productService.searchByProductName(productName);
            model.addAttribute("products", products);
        }
        return "home";
    }
}
