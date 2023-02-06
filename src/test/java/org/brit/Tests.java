package org.brit;

import com.codeborne.selenide.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.*;

public class Tests {

    @Test
    public void test() throws IOException {
        Configuration.browser = PWDriverProvider.class.getName();
        Configuration.headless = false;
        //  Configuration.browserSize = "1960x1080";
        open("http://the-internet.herokuapp.com/login");
        //Selenide.webdriver().driver().getWebDriver().manage().window().maximize();
        $(byId("content"))
                .$("#login")
                .$x(".//input[@name='username']")
                .setValue("tomsmith");
        $(byId("password"))
                .setValue("SuperSecretPassword!");
        $("button.radius").click();
        $("#flash.success")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("You logged into a secure area!"));
        File screenshot = screenshot(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File("screen.png"));
        sleep(2000);
    }


    @Test
    public void test1() {
        Configuration.browser = PWDriverProvider.class.getName();
        Configuration.headless = false;
        //  Configuration.browserSize = "1960x1080";
        open("http://the-internet.herokuapp.com/checkboxes");
        ElementsCollection selenideElements = $$("input");
        for (SelenideElement element: selenideElements){
            System.out.println(element.val());
        }

        webdriver().object().manage().addCookie(new Cookie("Some", "eeeeee"));
        webdriver().object().manage().deleteCookieNamed("Some");

    }

    @Test
    public void rest2(){
        Configuration.browser = PWDriverProvider.class.getName();
        Configuration.headless = false;
        Configuration.timeout = 15000;

        open("http://the-internet.herokuapp.com/dynamic_loading/1");
        $("#start button").click();
        Wait().until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
        String h4 = $("#finish")
                .$("h4").text();

        assertThat(h4).isEqualTo("Hello World!");
    }


    @Test
    public void rest3() throws IOException {
        Configuration.browser = PWDriverProvider.class.getName();
        Configuration.headless = false;
        Configuration.timeout = 15000;

        open("http://the-internet.herokuapp.com/dynamic_loading/1");
        sleep(3000);

        File screenshot1 = $("#start button").screenshot();
        //File screenshot = screenshot(OutputType.FILE);
        FileUtils.copyFile(screenshot1, new File("screen.png"));
    }
}
