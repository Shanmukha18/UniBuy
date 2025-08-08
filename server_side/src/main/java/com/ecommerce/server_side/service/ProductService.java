package com.ecommerce.server_side.service;

import com.ecommerce.server_side.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO dto);
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO updateProduct(Long id, ProductDTO dto);
    void deleteProduct(Long id);
}
