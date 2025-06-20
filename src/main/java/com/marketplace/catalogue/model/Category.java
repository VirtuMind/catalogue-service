package com.marketplace.catalogue.model;

import jakarta.persistence.*;


import java.util.UUID;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    private UUID id;
    
    @Column(nullable = false, length = 100, unique = true)
    private String name;
}
