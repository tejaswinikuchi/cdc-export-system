package com.example.cdc.service;

import com.example.cdc.entity.User;
import com.example.cdc.entity.Watermark;
import com.example.cdc.repository.UserRepository;
import com.example.cdc.repository.WatermarkRepository;
import com.example.cdc.util.CsvWriterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final UserRepository userRepository;
    private final WatermarkRepository watermarkRepository;

    @Async
    @Transactional
    public void fullExportAsync(String consumerId, String jobId, String filename) {

        log.info("Export job started: jobId={}, consumerId={}, type=FULL",
                jobId, consumerId);

        try {

            List<User> users = userRepository.findByIsDeletedFalse();

            String filePath = "/app/output/" + filename;

            CsvWriterUtil.writeFullExport(filePath, users);

            if (!users.isEmpty()) {
                OffsetDateTime maxTimestamp = users.stream()
                        .map(User::getUpdatedAt)
                        .max(OffsetDateTime::compareTo)
                        .get();

                updateWatermark(consumerId, maxTimestamp);
            }

            log.info("Export job completed: jobId={}, rowsExported={}",
                    jobId, users.size());

        } catch (Exception e) {

            log.error("Export job failed: jobId={}, error={}",
                    jobId, e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Async
    @Transactional
    public void incrementalExportAsync(String consumerId, String jobId, String filename) {

        log.info("Export job started: jobId={}, consumerId={}, type=INCREMENTAL",
                jobId, consumerId);

        try {

            OffsetDateTime lastWatermark = watermarkRepository
                    .findByConsumerId(consumerId)
                    .map(Watermark::getLastExportedAt)
                    .orElse(OffsetDateTime.MIN);

            List<User> users =
                    userRepository.findByUpdatedAtAfterAndIsDeletedFalse(lastWatermark);

            String filePath = "/app/output/" + filename;

            CsvWriterUtil.writeFullExport(filePath, users);

            if (!users.isEmpty()) {
                OffsetDateTime maxTimestamp = users.stream()
                        .map(User::getUpdatedAt)
                        .max(OffsetDateTime::compareTo)
                        .get();

                updateWatermark(consumerId, maxTimestamp);
            }

            log.info("Export job completed: jobId={}, rowsExported={}",
                    jobId, users.size());

        } catch (Exception e) {

            log.error("Export job failed: jobId={}, error={}",
                    jobId, e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Async
    @Transactional
    public void deltaExportAsync(String consumerId, String jobId, String filename) {

        log.info("Export job started: jobId={}, consumerId={}, type=DELTA",
                jobId, consumerId);

        try {

            OffsetDateTime lastWatermark = watermarkRepository
                    .findByConsumerId(consumerId)
                    .map(Watermark::getLastExportedAt)
                    .orElse(OffsetDateTime.MIN);

            List<User> users =
                    userRepository.findByUpdatedAtAfter(lastWatermark);

            String filePath = "/app/output/" + filename;

            CsvWriterUtil.writeDeltaExport(filePath, users);

            // -----------------------------
            // SIMULATED FAILURE FOR TESTING
            // -----------------------------
        //     if (true) {
        //         throw new RuntimeException("Simulated failure after file write");
        //     }

            // -----------------------------

            if (!users.isEmpty()) {
                OffsetDateTime maxTimestamp = users.stream()
                        .map(User::getUpdatedAt)
                        .max(OffsetDateTime::compareTo)
                        .get();

                updateWatermark(consumerId, maxTimestamp);
            }

            log.info("Export job completed: jobId={}, rowsExported={}",
                    jobId, users.size());

        } catch (Exception e) {

            log.error("Export job failed: jobId={}, error={}",
                    jobId, e.getMessage());

            throw new RuntimeException(e);
        }
    }

    public Optional<OffsetDateTime> getWatermark(String consumerId) {
        return watermarkRepository
                .findByConsumerId(consumerId)
                .map(Watermark::getLastExportedAt);
    }

    private void updateWatermark(String consumerId,
                                 OffsetDateTime timestamp) {

        Watermark watermark = watermarkRepository
                .findByConsumerId(consumerId)
                .orElse(new Watermark());

        watermark.setConsumerId(consumerId);
        watermark.setLastExportedAt(timestamp);
        watermark.setUpdatedAt(OffsetDateTime.now());

        watermarkRepository.save(watermark);
    }
}