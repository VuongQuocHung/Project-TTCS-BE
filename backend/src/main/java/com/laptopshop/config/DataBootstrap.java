package com.laptopshop.config;

import com.laptopshop.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataBootstrap implements CommandLineRunner {

    private final DataImportService importService;

    @Value("${app.import.enabled:false}")
    private boolean importEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (importEnabled) {
            log.info("Starting one-time data import...");
            try {
                importService.importData();
                log.info("Data import completed successfully!");
            } catch (Exception e) {
                log.error("Failed to import data: {}", e.getMessage(), e);
            }
        }
    }
}
