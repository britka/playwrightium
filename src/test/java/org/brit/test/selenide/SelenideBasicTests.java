package org.brit.test.selenide;

import com.codeborne.selenide.*;
import com.github.javafaker.Faker;
import org.brit.driver.PWDriverProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideBasicTests {
    @Test
    public void basicFunctionalTests() {

        Configuration.browser = PWDriverProvider.class.getName();
        Configuration.timeout = 10000;

        final String url = "https://testpages.herokuapp.com/styled/basic-html-form-test.html";

        open(url);

        Faker faker = new Faker();

        String name = faker.internet().emailAddress("some+2334777");
        String password = faker.internet().password();

        $(By.name("username")).setValue(name);
        $x("//input[@name='password']").setValue(password);

        String paragraph = faker.lorem().paragraph(5);
        $("textarea[name=comments]").setValue(paragraph);

        int checkBoxNum = faker.number().numberBetween(1, 4);
        ElementsCollection checkboxes = $$x("//input[@type='checkbox']");
        checkboxes.filter(Condition.checked).forEach(SelenideElement::click);

        SelenideElement checkBoxChecked = checkboxes.get(checkBoxNum - 1);
        SelenideElement checkBoxChecked1 = checkboxes.get(randomIntExcept(1, 4, checkBoxNum) - 1);
        String checkBoxValue = checkBoxChecked.attr("value");
        String checkBoxValue1 = checkBoxChecked1.getValue();
        checkBoxChecked.click();
        checkBoxChecked1.click();

        int radioButtonNum = faker.number().numberBetween(1, 4);
        SelenideElement radioButton = $$x("//input[@type='radio']")
                .find(Condition.value("rd" + radioButtonNum));
        String radioButtonValue = radioButton.getValue();
        radioButton.click();

        int firstItem = faker.number().numberBetween(0, 4);
        int secondItem = randomIntExcept(0, 4, firstItem);

        SelenideElement selectMulti = $(By.name("multipleselect[]"));

        selectMulti.selectOption(firstItem, secondItem);
        List<String> selectMultyValues = selectMulti.getSelectedOptions().attributes("value");

        SelenideElement select = $(By.name("dropdown"));
        select.selectOptionByValue("dd" + faker.number().numberBetween(1, 7));
        String selectValue = select.getValue();

        File file = new File(getClass().getClassLoader().getResource("textLorem.txt").getFile());
        $(By.name("filename")).uploadFile(file);

        $$("input[value=submit]")
                .find(Condition.attribute("name", "submitbutton"))
                .click();
        $x("//h1[contains(.,'Processed Form Details')]").shouldBe(Condition.visible);

        assertThat(getWebElementTextById("_valueusername")).isEqualTo(name);
        assertThat(getWebElementTextById("_valuepassword")).isEqualTo(password);
        assertThat(getWebElementTextById("_valuecomments")).isEqualTo(paragraph);
        assertThat(getWebElementsTextsBy("_valuecheckboxes"))
                .asList()
                .hasSameElementsAs(List.of(checkBoxValue, checkBoxValue1));
        assertThat(getWebElementTextById("_valueradioval")).isEqualTo(radioButtonValue);
        assertThat(getWebElementsTextsBy("_valuemultipleselect")).asList().isEqualTo(selectMultyValues);
        assertThat(getWebElementTextById("_valuedropdown")).isEqualTo(selectValue);

    }


    @ParameterizedTest
    @MethodSource("dataProvider")
    public void framesTest(String frameName, String frameText, int elementsCount) {
        open("https://testpages.eviltester.com/styled/frames/frames-test.html");
        switchTo().frame(frameName);
        $(By.xpath("//h1")).shouldHave(Condition.exactText(frameText));
        $$(By.xpath("//ul/li")).shouldHave(CollectionCondition.size(elementsCount));
        switchTo().defaultContent();
        $$x("//frame").shouldHave(CollectionCondition.size(5));
    }


    private static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.of("left", "Left", 30),
                Arguments.of("middle", "Middle", 40),
                Arguments.of("right", "Right", 50)
        );
    }

    private String getWebElementTextById(String id) {
        return $(By.id(id)).shouldBe(Condition.visible).text();
    }

    private List<String> getWebElementsTextsBy(String idMask) {
        return $$(By.xpath("//*[contains(@id, '%s')]".formatted(idMask)))
                .shouldHave(CollectionCondition.sizeGreaterThan(1))
                .texts();
    }

    private int randomIntExcept(int start, int finish, int except) {
        Faker faker = new Faker();
        while (true) {
            int result = faker.number().numberBetween(start, finish);
            if (result != except) {
                return result;
            }
        }
    }
}
