package com.example.cdc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "watermarks")
@Getter
@Setter
public class Watermark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_id", nullable = false, unique = true)
    private String consumerId;

    @Column(name = "last_exported_at", nullable = false)
    private OffsetDateTime lastExportedAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}