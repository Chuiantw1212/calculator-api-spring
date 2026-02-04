package com.en_chu.calculator_api_spring.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final MeterRegistry meterRegistry;

    public Map<String, Object> getServerStatus() {
        Map<String, Object> status = new HashMap<>();

        // 1. JVM 記憶體使用情況
        long memoryUsed = (long) meterRegistry.get("jvm.memory.used").gauge().value();
        long memoryMax = (long) meterRegistry.get("jvm.memory.max").gauge().value();
        status.put("memory_used_mb", toMB(memoryUsed));
        status.put("memory_max_mb", toMB(memoryMax));
        if (memoryMax > 0) {
            status.put("memory_usage_percentage", String.format("%.2f%%", (double) memoryUsed / memoryMax * 100));
        }

        // 2. CPU 使用率
        Double cpuUsage = meterRegistry.get("system.cpu.usage").gauge().value();
        status.put("cpu_usage_percentage", String.format("%.2f%%", cpuUsage * 100));

        // 3. 應用程式運行時間
        long uptimeSeconds = (long) meterRegistry.get("process.uptime").time(TimeUnit.SECONDS);
        status.put("uptime_seconds", uptimeSeconds);
        status.put("uptime_formatted", formatUptime(uptimeSeconds));

        // 4. 當前線程數
        long threadCount = (long) meterRegistry.get("jvm.threads.live").gauge().value();
        status.put("live_threads", threadCount);

        return status;
    }

    private long toMB(long bytes) {
        return bytes / (1024 * 1024);
    }

    private String formatUptime(long totalSeconds) {
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }
}
