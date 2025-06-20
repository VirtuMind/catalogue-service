package com.marketplace.catalogue.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id
    private UUID id;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;
    
//    @Column(name = "media_type", nullable = false, length = 20)
//    @Enumerated(EnumType.STRING)
//    private MediaType mediaType;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Enum for media_type values
//    public enum MediaType {
//        image, video
//    }
}
