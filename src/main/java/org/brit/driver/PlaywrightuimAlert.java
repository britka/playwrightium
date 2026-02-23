package org.brit.driver;

import com.microsoft.playwright.Dialog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Alert;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Serhii Bryt
 *
 * This is implementation for alerts actions
 * @see <a href='https://playwright.dev/java/docs/dialogs'>Playwright. Using dialigs(alerts)</a>
 *
 */
public class PlaywrightuimAlert implements Alert {

    private final PlaywrightiumDriver playwrightiumDriver;
    private final AtomicReference<@Nullable String> text = new AtomicReference<>();
    private final LinkedList<AlertAction> alertActions = new LinkedList<>();
    private final Consumer<Dialog> handler;
    private final AtomicReference<@Nullable String> textToSend = new AtomicReference<>();

    private final Consumer<Dialog> off = Dialog::dismiss;


    public PlaywrightuimAlert(PlaywrightiumDriver playwrightiumDriver) {
        this.playwrightiumDriver = playwrightiumDriver;
        textToSend.set(null);
        playwrightiumDriver.page.offDialog(off);
        handler = new Consumer<>() {
            @Override
            public void accept(Dialog dialog) {
                alertActions.forEach(action -> {
                    switch (action.action()) {
                        case "dismiss" -> {
                            playwrightiumDriver.page.offDialog(this);
                            dialog.dismiss();
                        }
                        case "accept" -> {
                            if (textToSend.get() != null) {
                                dialog.accept(textToSend.get());
                            } else {
                                dialog.accept();
                            }
                        }
                        case "getText" -> text.set(dialog.message());
                        case "sendKeys" -> textToSend.set(action.sendKeys);
                    }
                });
            }
        };
        alertActions.add(new AlertAction().action("getText"));
        addToHandler();
    }

    @Override
    public void dismiss() {
        alertActions.add(new AlertAction().action("dismiss"));
    }

    @Override
    public void accept() {
        alertActions.add(new AlertAction().action("accept"));
    }

    @Override
    public String getText() {
        return text.get();
    }

    @Override
    public void sendKeys(String keysToSend) {
        alertActions.add(new AlertAction().action("sendKeys").sendKeys(keysToSend));
    }

    private void addToHandler() {
        playwrightiumDriver.page.offDialog(off);
        playwrightiumDriver.page.onceDialog(handler);
    }

    @Data
    @Accessors(fluent = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlertAction {
        public String action;
        public String sendKeys;
    }
}
