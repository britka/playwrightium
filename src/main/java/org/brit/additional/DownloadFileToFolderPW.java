package org.brit.additional;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.WebElementSource;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadFileToFolderPW extends DownloadFileToFolder {

    public DownloadFileToFolderPW() {
        super();
    }

    @Nonnull
    @Override
    public File download(WebElementSource anyClickableElement, WebElement clickable, long timeout, long incrementTimeout, FileFilter fileFilter, DownloadAction action) {
        try {
            return myDownload(anyClickableElement, (PlaywrightWebElement) clickable, timeout, incrementTimeout, fileFilter, action);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public File myDownload(WebElementSource anyClickableElement, PlaywrightWebElement clickable, long timeout, long incrementTimeout, FileFilter fileFilter, DownloadAction action) throws FileNotFoundException {
        Page page = clickable.getLocator().page();
        Download download = page.waitForDownload(clickable::click);
        Path fileSaved = Paths.get(Configuration.downloadsFolder + "/tempDowloaded.tmp");
        download.saveAs(fileSaved);
        return super.archiveFile(anyClickableElement.driver(), fileSaved.toFile());
    }

}
