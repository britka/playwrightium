package org.brit.emulation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by Serhii Bryt
 * 29.03.2024 14:05
 **/
public class DeviceUtils {
    public static Device getDeviceByName(String deviceByName) {
        try {
            File file = new File(Device.class.getClassLoader().getResource("devices/deviceDescriptorsSource.json").getPath());
            String json = FileUtils.readFileToString(file, Charset.defaultCharset());
            Map<String, Device> map = new Gson().fromJson(json, new TypeToken<Map<String, Device>>() {
            }.getType());
            return map.get(deviceByName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

