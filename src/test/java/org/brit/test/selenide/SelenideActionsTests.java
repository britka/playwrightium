package org.brit.test.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.brit.driver.PWDriverProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.WheelInput;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideActionsTests {

    @BeforeAll
    public static void beforeAll() {
        Configuration.browser = PWDriverProvider.class.getName();
    }

    @BeforeEach
    public void beforeEach() {
        open("https://the-internet.herokuapp.com/large");
    }

    @Test
    public void scrollByAmountTest() {
        actions()
                .scrollByAmount(0, 100)
                .pause(500)
                .build()
                .perform();
        assertThat(getVisualViewportPageTop()).isEqualTo(100);
        assertThat(getVisualViewportPageLeft()).isEqualTo(0);

        actions()
                .scrollByAmount(150, 0)
                .pause(Duration.ofMillis(500))
                .perform();
        assertThat(getVisualViewportPageTop()).isEqualTo(100);
        assertThat(getVisualViewportPageLeft()).isBetween(150 - 1.0, 150 + 1.0);

        actions()
                .scrollByAmount(-40, -50)
                .pause(Duration.ofMillis(500))
                .perform();
        assertThat(getVisualViewportPageTop()).isBetween(50 - 1.0, 50 + 1.0);
        assertThat(getVisualViewportPageLeft()).isBetween(110 - 1.0, 110 + 1.0);
    }

    @Test
    public void scrollToElement() {
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
        assertThat(getVisualViewportPageTop()).isBetween(300 + 50 - 1.0, 300 + 50 + 1.0);
        assertThat(getVisualViewportPageLeft()).isBetween(250 + 40 - 1.0, 250 + 40 + 1.0);
    }

    @Test
    public void scrollFromOriginViewPort_deltaX_not0_deltaY_not0_xOffset_is0_yOffset_is0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport();
        actions()
                .scrollFromOrigin(scrollOrigin, 40, 50)
                .pause(Duration.ofMillis(500))
                .perform();
        assertThat(getVisualViewportPageTop()).isBetween(50 - 1.0, 50 + 1.0);
        assertThat(getVisualViewportPageLeft()).isBetween(40 - 1.0, 40 + 1.0);
    }

    @Test
    public void scrollFromOriginViewPort_deltaX_is0_deltaY_is0_xOffset_not0_yOffset_not0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport(40, 50);
        actions()
                .scrollFromOrigin(scrollOrigin, 0, 0)
                .pause(500)
                .perform();
        assertThat(getVisualViewportPageTop()).isBetween(50 - 1.0, 50 + 1.0);
        assertThat(getVisualViewportPageLeft()).isBetween(40 - 1.0, 40 + 1.0);
    }

    @Test
    public void scrollFromOriginViewPort_deltaX_isNegative_deltaY_isNegative_xOffset_not0_yOffset_not0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport(40, 50);
        actions()
                .scrollFromOrigin(scrollOrigin, -20, -30)
                .pause(500)
                .perform();
        assertThat(getVisualViewportPageTop()).isBetween(50 - 30 - 1.0, 50 - 30 + 1.0);
        assertThat(getVisualViewportPageLeft()).isBetween(40 - 20 - 1.0, 40 - 20 + 1.0);
    }


    @Test
    public void scrollFromWebElement_deltaX_isNegative_deltaY_isNegative_xOffset_0_yOffset_0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement($("#large-table"));
        actions()
                .scrollFromOrigin(scrollOrigin, -20, -30)
                .pause(500)
                .perform();
        DOMRect elementBoundaryBox = getElementBoundaryBox($("#large-table"));
        assertThat(elementBoundaryBox.getX()).isBetween(20 - 1.0, 20 + 1.0);
        assertThat(elementBoundaryBox.getY()).isBetween(30 - 1.0, 30 + 1.0);
    }

    @Test
    public void scrollFromWebElement_deltaX_isNegative_deltaY_isNegative_xOffset_not0_yOffset_not0() {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement($("#large-table"), 20, 30);
        actions()
                .scrollFromOrigin(scrollOrigin, -20, -30)
                .pause(500)
                .perform();
        DOMRect elementBoundaryBox = getElementBoundaryBox($("#large-table"));
        assertThat(elementBoundaryBox.getX()).isBetween(-1.0, 1.0);
        assertThat(elementBoundaryBox.getY()).isBetween(-1.0, 1.0);
    }

    private Boolean isElementVisibleInViewPort(SelenideElement element) {
        File script = new File(getClass().getClassLoader().getResource("is-element-visible.js").getPath());
        try {
            return Boolean.parseBoolean(executeJavaScript(FileUtils.readFileToString(script, Charset.defaultCharset()), element));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Double getVisualViewportPageTop() {
        return Double.parseDouble(executeJavaScript("return window.visualViewport.pageTop;").toString());
    }

    private Double getVisualViewportPageLeft() {
        return Double.parseDouble(executeJavaScript("return window.visualViewport.pageLeft;").toString());
    }

    private DOMRect getElementBoundaryBox(SelenideElement element) {
        final ObjectMapper mapper = new ObjectMapper();
        Object o = executeJavaScript("return arguments[0].getBoundingClientRect();", element);
        return mapper.convertValue(o, DOMRect.class);
    }

    static class DOMRect {
        private Double x, y, left, right, bottom, top, width, height;

        public DOMRect() {
        }

        public DOMRect(Double x, Double y, Double left, Double right, Double bottom, Double top, Double width, Double height) {
            this.x = x;
            this.y = y;
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            this.width = width;
            this.height = height;
        }

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }

        public Double getLeft() {
            return left;
        }

        public void setLeft(Double left) {
            this.left = left;
        }

        public Double getRight() {
            return right;
        }

        public void setRight(Double right) {
            this.right = right;
        }

        public Double getBottom() {
            return bottom;
        }

        public void setBottom(Double bottom) {
            this.bottom = bottom;
        }

        public Double getTop() {
            return top;
        }

        public void setTop(Double top) {
            this.top = top;
        }

        public Double getWidth() {
            return width;
        }

        public void setWidth(Double width) {
            this.width = width;
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DOMRect domRect = (DOMRect) o;
            return Objects.equals(x, domRect.x) && Objects.equals(y, domRect.y) && Objects.equals(left, domRect.left) && Objects.equals(right, domRect.right) && Objects.equals(bottom, domRect.bottom) && Objects.equals(top, domRect.top) && Objects.equals(width, domRect.width) && Objects.equals(height, domRect.height);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, left, right, bottom, top, width, height);
        }
    }

}
