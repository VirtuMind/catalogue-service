package com.marketplace.catalogue.model;

import jakarta.persistence.*;
import lombok.Data;


import java.util.UUID;

@Entity
@Data
@Table(name = "categories")
public class Category {
    @Id
    private UUID id;
    
    @Column(nullable = false, length = 100, unique = true)
    private String name;
}
