package org.brit.options;

import com.microsoft.playwright.Tracing;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Serhii Bryt
 * 24.06.2024 15:45
 **/
public class TracingOptions {
    private Tracing.StartOptions startOptions = new Tracing.StartOptions();
    private Tracing.StopOptions stopOptions = new Tracing.StopOptions();

    public TracingOptions() {
        startOptions
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true);
        stopOptions
                .setPath(Path.of("tracing/tracing.zip"));
    }

    public Tracing.StartOptions getStartOptions() {
        return startOptions;
    }

    public Tracing.StopOptions getStopOptions() {
        return stopOptions;
    }
}
