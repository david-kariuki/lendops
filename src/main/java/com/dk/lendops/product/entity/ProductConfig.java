package com.dk.lendops.product.entity;

import com.dk.lendops.product.enums.ConfigType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Product Config Entity
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product_config",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_config_type_version",
                        columnNames = {"product_id", "config_type", "config_version"})
        })
public class ProductConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "config_type", nullable = false, length = 50)
    private ConfigType configType;

    @Column(name = "config_version", nullable = false)
    private Integer configVersion;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "config_json", nullable = false, columnDefinition = "json")
    private String configJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
