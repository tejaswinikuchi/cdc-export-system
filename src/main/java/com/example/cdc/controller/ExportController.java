package com.example.cdc.controller;

import com.example.cdc.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/exports")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/full")
    public ResponseEntity<Map<String, Object>> fullExport(
            @RequestHeader("X-Consumer-ID") String consumerId) {

        String jobId = UUID.randomUUID().toString();
        String filename = "full_" + consumerId + "_" + Instant.now().toEpochMilli() + ".csv";

        exportService.fullExportAsync(consumerId, jobId, filename);

        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("status", "started");
        response.put("exportType", "full");
        response.put("outputFilename", filename);

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/incremental")
    public ResponseEntity<Map<String, Object>> incrementalExport(
            @RequestHeader("X-Consumer-ID") String consumerId) {

        String jobId = UUID.randomUUID().toString();
        String filename = "incremental_" + consumerId + "_" + Instant.now().toEpochMilli() + ".csv";

        exportService.incrementalExportAsync(consumerId, jobId, filename);

        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("status", "started");
        response.put("exportType", "incremental");
        response.put("outputFilename", filename);

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/delta")
    public ResponseEntity<Map<String, Object>> deltaExport(
            @RequestHeader("X-Consumer-ID") String consumerId) {

        String jobId = UUID.randomUUID().toString();
        String filename = "delta_" + consumerId + "_" + Instant.now().toEpochMilli() + ".csv";

        exportService.deltaExportAsync(consumerId, jobId, filename);

        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("status", "started");
        response.put("exportType", "delta");
        response.put("outputFilename", filename);

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/watermark")
    public ResponseEntity<Map<String, Object>> getWatermark(
            @RequestHeader("X-Consumer-ID") String consumerId) {

        return exportService.getWatermark(consumerId)
                .map(w -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("consumerId", consumerId);
                    response.put("lastExportedAt", w.toString());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}