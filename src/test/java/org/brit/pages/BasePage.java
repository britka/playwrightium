package org.brit.pages;

public class BasePage {
    public MainMenu mainMenu(){
        return new MainMenu();
    }

    public PageMenu pageMenu(){
        return new PageMenu();
    }
}
