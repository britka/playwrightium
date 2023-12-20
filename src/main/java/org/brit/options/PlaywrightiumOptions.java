package org.brit.options;

import org.openqa.selenium.MutableCapabilities;

import java.nio.file.Path;

public class PlaywrightiumOptions extends MutableCapabilities {

    public PlaywrightiumOptions() {
        super();
        setHeadless(false);
        setBrowserName("chromium");
    }

    public void setHeadless(boolean headless) {
        setCapability("headless", headless);
    }

    public Boolean getHeadless() {
        return (Boolean) getCapability("headless");
    }

    public void setBrowserName(String browserName) {
        setCapability("browserName", browserName);
    }

    public void setRecordVideo(Boolean recordVideo){
        setCapability("recordVideo", recordVideo);
    }

    public void setRecordsFolder(Path recordsFolder){
        setCapability("recordsFolder", recordsFolder);
    }
}
