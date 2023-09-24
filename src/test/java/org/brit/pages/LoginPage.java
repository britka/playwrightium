package org.brit.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.lang.reflect.InvocationTargetException;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage{

    public BasePage login(String login, String password){
        return login(login, password, BasePage.class);
    }

    public LoginPage unSuccessLogin(String login, String password){
        return login(login, password, LoginPage.class);
    }

    private  <T extends BasePage> T login(String login, String password, Class<T> pageToReturn){
        $("#Email").setValue(login);
        $("#Password").setValue(password);
        $(".login-button").click();
        try {
            return pageToReturn.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmailValidationError(){
        return $("#Email-error").text();
    }

    public String getLoginValidationError(){
        return $(".message-error.validation-summary-errors").text();
    }

    public LoginPage rememberMe(boolean check){
        SelenideElement rememberMeCheckBox = $("#RememberMe");
        if (check){
            if (rememberMeCheckBox.is(Condition.not(Condition.checked))){
                rememberMeCheckBox.click();
            }
        } else {
            if (rememberMeCheckBox.is(Condition.checked)){
                rememberMeCheckBox.click();
            }
        }
        return this;
    }
}
