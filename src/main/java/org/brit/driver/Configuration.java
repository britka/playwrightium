package org.brit.driver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {
    /**
     * Start headless
     * Default value: true
     */
    public static boolean headless = true;

    /**
     * Set up browser for automation tests
     * Available browses for now:
     * chromium, firefox (gecko), safari (webkit)
     * Default value: chromium
     */
    public static String browser = "chromium";

    /**
     * Default timeout for tests
     * Default value: 30000 ms
     */
    public static double defaultTimeout = 30000;

    /**
     * Default folder for downloads
     * Default value: Paths.get("downloads")
     */
    public static Path downloadsPath = Paths.get("downloads");

    /**
     * Accept downloads or not
     * Default value: true
     */
    public static boolean acceptDownloads = true;

    /**
     * SlowMo timeout
     * Default value: 0
     */
    public static double slowMoTimeout = 0;

    /**
     * Base url
     * Default value: empty string
     */
    public static String baseUrl = "";

}
