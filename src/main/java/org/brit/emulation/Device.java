package org.brit.emulation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.options.ViewportSize;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Serhii Bryt
 * 29.03.2024 13:50
 **/
@Data
public class Device {
    private String userAgent;
    private ViewportSize viewport;
    private double deviceScaleFactor;
    private boolean isMobile;
    private boolean hasTouch;
    private String defaultBrowserType;

    public static void main(String[] args) throws IOException {
        File file = new File(Device.class.getClassLoader().getResource("devices/deviceDescriptorsSource.json").getPath());
        String json = FileUtils.readFileToString(file, Charset.defaultCharset());
        Gson gson = new Gson();
        Map<String, Device> map = gson.fromJson(json, new TypeToken<Map<String, Device>>() {
        }.getType());
    }
}
