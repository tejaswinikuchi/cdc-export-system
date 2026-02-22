package com.example.cdc.repository;

import com.example.cdc.entity.Watermark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatermarkRepository extends JpaRepository<Watermark, Long> {

    Optional<Watermark> findByConsumerId(String consumerId);
}