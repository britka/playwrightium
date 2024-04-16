# Changelog
## 1.3.0
* Geolocation added
* Device emulation added
* Permissions added
* Locale added
* TimeZone added

## 1.2.1 (release 28.03.24)
* Update `sendKeys` method. Now you can do such things as  
```java
webElement.sendKeys("Test", Keys.ENTER);
```
* Update tests
* Update dependencies
* Update github workflows

## 1.2.0 (release 10.03.24)
* Update Playwrightium options:
  * Add `recordVideo` capability to indicate weather or not to record a video
  * Add `recordsFolder` capability to set folder where videos should be saved
  * Add ability to run tests remotely for Selenoid, Selenium Grid, Aerokube Moon
* Add method to check if we need to save video when quit driver
* Add templates in tests for initializing driver for remote use and for video recording.
* Add tests
* Update github workflow file to run separately local and remote tests

## 1.1.1 (release 05.01.24)
* rebuild javaScript executor. Now if script result is object it returns as JSON
* add implementation "scroll" action. 
* add dependbot.xml
* update tests

## 1.1.0 (released 20.12.23)
* add support of Playwright locators:
  * getByRole
  * getByTestId
  * getByLabel
  * getByPlaceholder
  * getByText
  * getByTitle
* add support of video recording
* update Actions class
* update PlaywrightiumOptions
* update tests

## 1.0.0 (released 11.12.2023)
* First release