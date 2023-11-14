package org.brit.test;

import org.brit.additional.PlaywrightiumSelect;
import org.brit.driver.PlaywrightiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ISelect;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;

public class NopCommerceTests {
    ///https://testpages.eviltester.com/styled/index.html

    @Test
    public void test() {
        WebDriver driver = new PlaywrightiumDriver();
        driver.get("https://testpages.herokuapp.com/styled/basic-html-form-test.html");

        ISelect dropdown = new PlaywrightiumSelect(driver.findElement(By.name("multipleselect[]")));

        dropdown.selectByVisibleText("Selection Item 2");
        sleep(1000);
        dropdown.selectByValue("ms3");
        sleep(1000);
        dropdown.deselectByIndex(2);
        sleep(1000);
        dropdown.selectByIndex(3);
        sleep(1000);

        List<WebElement> options = dropdown.getOptions();
        options.forEach(p -> System.out.println(p.getText()));
        List<WebElement> allSelectedOptions = dropdown.getAllSelectedOptions();
        allSelectedOptions.forEach(p -> System.out.println(p.getText()));


    }


    @Test
    public void test1() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://testpages.herokuapp.com/styled/basic-html-form-test.html");

        ISelect dropdown = new Select(driver.findElement(By.name("multipleselect[]")));

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

