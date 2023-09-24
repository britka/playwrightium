package org.brit;

import org.apache.commons.io.FileUtils;
import org.brit.additional.PlaywrightiumSelect;
import org.brit.driver.PlaywrightWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ISelect;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;

public class NopCommerceTests {

    @Test
    public void test() {
        WebDriver driver = new PlaywrightWebDriver();
        driver.get("https://testpages.herokuapp.com/styled/basic-html-form-test.html");

        ISelect dropdown = new PlaywrightiumSelect(driver.findElement(By.name("multipleselect[]")));

//        new Actions(driver)
//                .
//                .keyDown("A")
//                .build()
//                .perform();
        dropdown.selectByVisibleText("Selection Item 2");
        sleep(1000);
        dropdown.selectByValue("ms3");
        sleep(1000);
        dropdown.deselectByIndex(2);
        sleep(1000);
        dropdown.selectByIndex(3);
        sleep(1000);

      //  new Actions(driver).keyUp(Keys.COMMAND).perform();

        List<WebElement> options = dropdown.getOptions();
        options.forEach(p -> System.out.println(p.getText()));
        List<WebElement> allSelectedOptions = dropdown.getAllSelectedOptions();
        allSelectedOptions.forEach(p -> System.out.println(p.getText()));


    }


}

