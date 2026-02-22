package com.example.cdc.service;

import com.example.cdc.entity.Watermark;
import com.example.cdc.repository.WatermarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WatermarkService {

    private final WatermarkRepository watermarkRepository;

    public Optional<Watermark> getWatermark(String consumerId) {
        return watermarkRepository.findByConsumerId(consumerId);
    }

    public Watermark createOrUpdate(String consumerId, OffsetDateTime timestamp) {

        Watermark watermark = watermarkRepository
                .findByConsumerId(consumerId)
                .orElseGet(() -> {
                    Watermark w = new Watermark();
                    w.setConsumerId(consumerId);
                    return w;
                });

        watermark.setLastExportedAt(timestamp);
        watermark.setUpdatedAt(OffsetDateTime.now());

        return watermarkRepository.save(watermark);
    }
}