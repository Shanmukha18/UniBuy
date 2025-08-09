package com.ecommerce.server_side.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private double price;
    private int stock;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;// optional for frontend use

    @Column(columnDefinition = "TEXT")
    private String categories; // JSON array of categories as string
}
