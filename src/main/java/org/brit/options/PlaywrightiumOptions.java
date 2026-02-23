package org.brit.options;

import com.microsoft.playwright.options.Geolocation;
import org.brit.emulation.Device;
import org.brit.permission.Permissions;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.MutableCapabilities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

/**
 * @author Serhii Bryt
 * This is Option class for Playwrightium
 */
public class PlaywrightiumOptions extends MutableCapabilities {

    /**
     * Default constructor
     * By default browser id chromium and not in headless mode
     */
    public PlaywrightiumOptions() {
        super();
        setHeadless(false);
        setIgnoreHTTPSErrors(false);
        setBrowserName("chromium");
        setRecordsFolder(Paths.get("build/video"));
        setSkipDownloadBrowsers(false);
    }

    /**
     * Sets headless mode
     */
    public void setHeadless(boolean headless) {
        setCapability("headless", headless);
    }

    @Nullable
    public Boolean getHeadless() {
        return (Boolean) getCapability("headless");
    }

    /**
     * Sets setIgnoreHTTPSErrors mode
     */
    public void setIgnoreHTTPSErrors(boolean value) {
        setCapability(ACCEPT_INSECURE_CERTS, value);
    }

    @Nullable
    public Boolean getIgnoreHTTPSErrors() {
        return (Boolean) getCapability(ACCEPT_INSECURE_CERTS);
    }

    /**
     * Sets browser name.
     * Available values are chromium, firefox, webkit
     * @param browserName browser name
     */
    public void setBrowserName(String browserName) {
        setCapability("browserName", browserName);
    }

    public void setBrowserName(Browsers browserName) {
        setCapability("browserName", browserName.getValue());
    }

    /**
     * Indicates whether to record video
     * @param recordVideo true - record video, false - not
     */
    public void setRecordVideo(Boolean recordVideo) {
        setCapability("recordVideo", recordVideo);
    }

    @Nullable
    public Boolean getRecordVideo() {
        return (Boolean) getCapability("recordVideo");
    }


    /**
     * The folder where to save video recordings. Default "build/videos".
     * This made to be compatible with Selenide
     * @param recordsFolder folder where to save video recordings
     */
    public void setRecordsFolder(Path recordsFolder) {
        setCapability("recordsFolder", recordsFolder);
    }

    @Nullable
    public Path getRecordsFolder() {
        return (Path) getCapability("recordsFolder");
    }

    /**
     * Needs only when Playwrightium is used for running tests remotely
     * For example, for Aerokube Selenoid, Aerokube Moon, Selenium Grid etc.
     * It sets up how we should connect to remotes, using regular method or websocket connection.
     * @param connectionByWS if true then we will connect using websocket, otherwise - regular method
     * @see <a href='https://aerokube.com/moon/latest/'>Aerokube Moon</a>
     * @see <a href='https://aerokube.com/selenoid/latest/'>Aerokube Selenoid</a>
     * @see <a href='https://www.selenium.dev/documentation/grid/'>Selenium grid</a>
     */
    public void setConnectionByWS(Boolean connectionByWS) {
        setCapability("connectionByWS", connectionByWS);
    }

    @Nullable
    public Boolean getConnectionByWS() {
        return (Boolean) getCapability("connectionByWS");
    }

    public void setEmulation(Device device) {
        setCapability("emulation", device);
    }

    @Nullable
    public Device getEmulation() {
        return (Device) getCapability("emulation");
    }

    public void setLocale(Locale locale) {
        setCapability("locale", locale);
    }

    @Nullable
    public Locale getLocale() {
        return (Locale) getCapability("locale");
    }

    public void setTimeZone(TimeZone timeZone) {
        setCapability("timeZone", timeZone);
    }

    @Nullable
    public TimeZone getTimeZone() {
        return (TimeZone) getCapability("timeZone");
    }

    public void setGeolocation(Geolocation geolocation) {
        setCapability("geolocation", geolocation);
    }

    @Nullable
    public Geolocation getGeolocation() {
        return (Geolocation) getCapability("geolocation");
    }

    public void setPermissions(List<Permissions> permissions) {
        setCapability("permissions", permissions);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public List<Permissions> getPermissions() {
        return (List<Permissions>) getCapability("permissions");
    }

    public void setEnableTracing(Boolean enableTracing) {
        setCapability("enableTracing", enableTracing);
    }

    @Nullable
    public Boolean getEnableTracing() {
       return (Boolean) getCapability("enableTracing");
    }

    public void setTracingOptions(TracingOptions tracingOptions) {
        setCapability("tracingOptions", tracingOptions);
    }

    @Nullable
    public TracingOptions getTracingOptions() {
        return (TracingOptions) getCapability("tracingOptions");
    }

    public void setSkipDownloadBrowsers(Boolean doNotDownloadBrowsers) {
        setCapability("skipDownloadBrowsers", doNotDownloadBrowsers);
    }

    @Nullable
    public Boolean getSkipDownloadBrowsers() {
        return (Boolean) getCapability("skipDownloadBrowsers");
    }

}
