package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import com.ecommerce.server_side.model.Product;
import org.springframework.stereotype.Service;
import com.ecommerce.server_side.repository.ProductRepository;
import com.ecommerce.server_side.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImplementation implements ProductService {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductDTO mapToDto(Product product) {
        List<String> categories = null;
        if (product.getCategories() != null && !product.getCategories().trim().isEmpty()) {
            try {
                categories = objectMapper.readValue(product.getCategories(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                // If JSON parsing fails, treat as single category
                categories = List.of(product.getCategories());
            }
        } else {
            categories = List.of();
        }

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categories(categories)
                .imageUrl(product.getImageUrl())
                .build();
    }

    private Product mapToEntity(ProductDTO dto) {
        String categoriesJson = null;
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            try {
                categoriesJson = objectMapper.writeValueAsString(dto.getCategories());
            } catch (JsonProcessingException e) {
                // If JSON serialization fails, store as comma-separated string
                categoriesJson = String.join(",", dto.getCategories());
            }
        }

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .categories(categoriesJson)
                .imageUrl(dto.getImageUrl())
                .build();
    }

    @Override
    public ProductDTO createProduct(ProductDTO dto) {
        Product saved = productRepository.save(mapToEntity(dto));
        return mapToDto(saved);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        
        // Handle categories
        String categoriesJson = null;
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            try {
                categoriesJson = objectMapper.writeValueAsString(dto.getCategories());
            } catch (JsonProcessingException e) {
                categoriesJson = String.join(",", dto.getCategories());
            }
        }
        existing.setCategories(categoriesJson);
        
        existing.setImageUrl(dto.getImageUrl());
        return mapToDto(productRepository.save(existing));
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
