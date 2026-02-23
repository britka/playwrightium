package org.brit.options;

import com.microsoft.playwright.Tracing;
import lombok.Getter;

import java.nio.file.Path;

/**
 * Created by Serhii Bryt
 * 24.06.2024 15:45
 **/
public class TracingOptions {
    @Getter
    private final Tracing.StartOptions startOptions = new Tracing.StartOptions();

    @Getter
    private final Tracing.StopOptions stopOptions = new Tracing.StopOptions();

    public TracingOptions() {
        startOptions
                .setScreenshots(true)
                .setSnapshots(false)
                .setSources(false);
        stopOptions
                .setPath(Path.of("tracing/tracing.zip"));
    }
}
