package com.ecommerce.server_side.controller;

import com.ecommerce.server_side.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.server_side.service.ProductService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO create(@RequestBody ProductDTO dto){
        return productService.createProduct(dto);
    }

    @GetMapping
    public List<ProductDTO> getAll(){
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDTO getOne(@PathVariable Long id){
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO update(@PathVariable Long id, @RequestBody ProductDTO dto){
        return productService.updateProduct(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id){
        productService.deleteProduct(id);
    }
}
