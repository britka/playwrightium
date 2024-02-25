package org.brit.options;

import org.openqa.selenium.MutableCapabilities;

import java.nio.file.Path;

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
        setRecordVideo(false);
        setBrowserName("chromium");
    }

    /**
     * Sets headless mode
     * @param headless
     */
    public void setHeadless(boolean headless) {
        setCapability("headless", headless);
    }

    public Boolean getHeadless() {
        return (Boolean) getCapability("headless");
    }

    /**
     * Sets browser name.
     * Available values are chromium, firefox, webkit
     * @param browserName browser name
     */
    public void setBrowserName(String browserName) {
        setCapability("browserName", browserName);
    }

    /**
     * Indicates whether to record video
     * @param recordVideo true - record video, false - not
     */
    public void setRecordVideo(Boolean recordVideo){
        setCapability("recordVideo", recordVideo);
    }

    public Boolean getRecordVideo(){
        return (Boolean) getCapability("recordVideo");
    }


    /**
     * The folder where to save video recordings. Default "build/videos".
     * This made to be compatible with Selenide
     * @param recordsFolder folder where to save video recordings
     */
    public void setRecordsFolder(Path recordsFolder){
        setCapability("recordsFolder", recordsFolder);
    }
    public Path getRecordsFolder(){
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
    public void setConnectionByWS(Boolean connectionByWS){
        setCapability("connectionByWS", connectionByWS);
    }

    public Boolean getConnectionByWS(){
        return (Boolean) getCapability("connectionByWS");
    }
}
