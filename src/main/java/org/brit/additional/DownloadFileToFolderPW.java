package org.brit.additional;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.WebElementSource;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Serhii Bryt
 *
 * This is implementation for file download to be compartible with Selenide
 */
public class DownloadFileToFolderPW extends DownloadFileToFolder {

    public DownloadFileToFolderPW() {
        super();
    }

    @Override
    public File download(WebElementSource anyClickableElement, WebElement clickable, long timeout, long incrementTimeout, FileFilter fileFilter, DownloadAction action) {
        try {
            return myDownload(anyClickableElement, (PlaywrightWebElement) clickable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File myDownload(WebElementSource anyClickableElement, PlaywrightWebElement clickable) throws IOException {
        Page page = clickable.getLocator().page();
        Download download = page.waitForDownload(clickable::click);
        Path fileSaved = Paths.get(Configuration.downloadsFolder, download.suggestedFilename());
        download.saveAs(fileSaved);
        Driver driver = anyClickableElement.driver();
        return super.archiveFile(driver.config(), driver.getWebDriver(), fileSaved.toFile());
    }

}
