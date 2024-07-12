package org.brit.test;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Serhii Bryt
 * 23.06.2024 18:35
 **/
public class Experiment {
    @Test
    public void test() {
        Playwright playwright = Playwright.create(new Playwright.CreateOptions()
                .setEnv(Map.of("DEBUG", "pw:browser")));

        Page firefox = playwright.firefox().launch(new BrowserType.LaunchOptions()
                        .setHeadless(false))
                  //      .setChannel("firefox")
                //.setExecutablePath(Path.of("C:\\Program Files\\Firefox Nightly\\firefox.exe")))
                .newContext()
                .newPage();
        firefox.navigate("https://www.google.com");
    }
}
