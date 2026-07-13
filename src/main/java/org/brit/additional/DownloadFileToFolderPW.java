package org.brit.additional;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Serhii Bryt
 * <p>
 * This is implementation for file download to be compartible with Selenide
 */
public class DownloadFileToFolderPW extends DownloadFileToFolder {

    public DownloadFileToFolderPW() {
        super();
    }

    @Override
    public List<File> download(Driver driver, WebElement clickable, long timeout, long requestedIncrementTimeout, DownloadOptions options) {
        try {
            return myDownload(driver, (PlaywrightWebElement) clickable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO return all downloaded files, not only one
    public List<File> myDownload(Driver driver, PlaywrightWebElement clickable) throws IOException {
        Page page = clickable.getLocator().page();
        Download download = page.waitForDownload(clickable::click);
        Path fileSaved = Paths.get(Configuration.downloadsFolder, download.suggestedFilename());
        download.saveAs(fileSaved);
        return Stream.of(fileSaved).map(file -> archive(driver, file)).toList();
    }

    // TODO Depending on ContentStrategy, store full file content or an empty file.
    private File archive(Driver driver, Path file) {
        try {
            return archiveFile(driver.config(), driver.getWebDriver(), file.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to archive the downloaded file " + file, e);
        }
    }

}
