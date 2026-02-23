package org.brit.test.selenide.local;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.testng.TextReport;
import org.brit.driver.PWDriverProvider;
import org.openqa.selenium.interactions.WheelInput;
import org.testng.annotations.*;

import java.io.IOException;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.TextCheck.FULL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.brit.test.selenide.Utils.js;
import static org.brit.test.selenide.Utils.resource;

@Listeners({TextReport.class})
public final class SelenideActionsTests {
    @BeforeClass
    public void beforeAll() {
        closeWebDriver();
        Configuration.textCheck = FULL_TEXT;
    }

    @BeforeMethod
    public void beforeEach() {
        Configuration.browser = PWDriverProvider.class.getName();
        open("https://the-internet.herokuapp.com/large");
    }

    @AfterClass
    public static void afterAll(){
        closeWebDriver();
    }

    @Test
    public void scrollByAmountTest() {
        actions()
                .scrollByAmount(0, 100)
                .pause(500)
                .build()
                .perform();
        Viewport viewport = getVisualViewport();
        assertThat(viewport.pageTop()).isEqualTo(100);
        assertThat(viewport.pageLeft()).isEqualTo(0);

        actions()
                .scrollByAmount(150, 0)
                .pause(Duration.ofMillis(500))
                .perform();
        Viewport viewport2 = getVisualViewport();
        assertThat(viewport2.pageTop()).isEqualTo(100);
        assertThat(viewport2.pageLeft()).isBetween(150 - 1.0, 150 + 1.0);

        actions()
                .scrollByAmount(-40, -50)
                .pause(Duration.ofMillis(500))
                .perform();
        Viewport viewport3 = getVisualViewport();
        assertThat(viewport3.pageTop()).isBetween(50 - 1.0, 50 + 1.0);
        assertThat(viewport3.pageLeft()).isBetween(110 - 1.0, 110 + 1.0);
    }

    @Test
    public void scrollToElement() throws IOException {
        actions()
                .scrollToElement($("#large-table"))
                .pause(500)
                .perform();
        assertThat(isElementVisibleInViewPort($("#large-table"))).isTrue();
    }

    @Test
    public void scrollFromOriginViewPort_deltaX_not0_deltaY_not0_xOffset_not0_yOffset_not0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport(250, 300);
        actions()
                .scrollFromOrigin(scrollOrigin, 40, 50)
                .pause(500)
                .perform();
        Viewport viewport = getVisualViewport();
        assertThat(viewport.pageTop()).isBetween(300 + 50 - 1.0, 300 + 50 + 1.0);
        assertThat(viewport.pageLeft()).isBetween(250 + 40 - 1.0, 250 + 40 + 1.0);
    }

    @Test
    public void scrollFromOriginViewPort_deltaX_not0_deltaY_not0_xOffset_is0_yOffset_is0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport();
        actions()
                .scrollFromOrigin(scrollOrigin, 40, 50)
                .pause(Duration.ofMillis(500))
                .perform();
        Viewport viewport = getVisualViewport();
        assertThat(viewport.pageTop()).isBetween(50 - 1.0, 50 + 1.0);
        assertThat(viewport.pageLeft()).isBetween(40 - 1.0, 40 + 1.0);
    }

    @Test
    public void scrollFromOriginViewPort_deltaX_is0_deltaY_is0_xOffset_not0_yOffset_not0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport(40, 50);
        actions()
                .scrollFromOrigin(scrollOrigin, 0, 0)
                .pause(500)
                .perform();
        Viewport viewport = getVisualViewport();
        assertThat(viewport.pageTop()).isBetween(50 - 1.0, 50 + 1.0);
        assertThat(viewport.pageLeft()).isBetween(40 - 1.0, 40 + 1.0);
    }

    @Test
    public void scrollFromOriginViewPort_deltaX_isNegative_deltaY_isNegative_xOffset_not0_yOffset_not0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport(40, 50);
        actions()
                .scrollFromOrigin(scrollOrigin, -20, -30)
                .pause(500)
                .perform();
        Viewport viewport = getVisualViewport();
        assertThat(viewport.pageTop()).isBetween(50 - 30 - 1.0, 50 - 30 + 1.0);
        assertThat(viewport.pageLeft()).isBetween(40 - 20 - 1.0, 40 - 20 + 1.0);
    }


    @Test
    public void scrollFromWebElement_deltaX_isNegative_deltaY_isNegative_xOffset_0_yOffset_0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement($("#large-table"));
        actions()
                .scrollFromOrigin(scrollOrigin, -20, -30)
                .pause(500)
                .perform();
        DOMRect elementBoundaryBox = getElementBoundaryBox($("#large-table"));
        assertThat(elementBoundaryBox.x()).isBetween(20 - 1.0, 20 + 1.0);
        assertThat(elementBoundaryBox.y()).isBetween(30 - 1.0, 30 + 1.0);
    }

    @Test
    public void scrollFromWebElement_deltaX_isNegative_deltaY_isNegative_xOffset_not0_yOffset_not0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement($("#large-table"), 20, 30);
        actions()
                .scrollFromOrigin(scrollOrigin, -20, -30)
                .pause(500)
                .perform();
        DOMRect elementBoundaryBox = getElementBoundaryBox($("#large-table"));
        assertThat(elementBoundaryBox.x).isBetween(-1.0, 1.0);
        assertThat(elementBoundaryBox.y()).isBetween(-1.0, 1.0);
    }

    private Boolean isElementVisibleInViewPort(SelenideElement element) throws IOException {
        String script = resource("/is-element-visible.js");
        return executeJavaScript(script, element);
    }

    private Viewport getVisualViewport() {
        return js("""
                return ({
                    "pageTop": window.visualViewport.pageTop,
                    "pageLeft": window.visualViewport.pageLeft
                })""", Viewport.class);
    }

    private DOMRect getElementBoundaryBox(SelenideElement element) {
        return js("return arguments[0].getBoundingClientRect();", DOMRect.class, element);
    }

    record Viewport(double pageTop, double pageLeft) {
    }

    record DOMRect(Double x, Double y, Double left, Double right, Double bottom, Double top, Double width, Double height) {
    }
}
