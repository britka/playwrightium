package org.brit.locators;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.microsoft.playwright.options.AriaRole;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.io.Serializable;
import java.util.List;

/**
 * @author Serhii Bryt
 * This is implementation to use Playwright getBy... methods.
 * @see <a href='https://playwright.dev/java/docs/locators'>Playwright. getBy... locators search</a>
 */
@Getter
public abstract class PlaywrightiumBy extends By implements By.Remotable {
    private final By.Remotable.Parameters remoteParameters;

    protected PlaywrightiumBy(String selector, AriaRole ariaRole, ArialSearchOptions pageAriaRoleOptions) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ariaRole.toString()), "Must supply a not empty locator value.");
        this.remoteParameters = new By.Remotable.Parameters(selector,
                List.of(ariaRole, pageAriaRoleOptions == null ? new ArialSearchOptions() : pageAriaRoleOptions));
    }

    protected PlaywrightiumBy(String selector, String value, Boolean exact) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "Must supply a not empty locator value.");
        this.remoteParameters = new By.Remotable.Parameters(selector, List.of(value, exact != null && exact));
    }

    public List<WebElement> findElements(SearchContext context) {
        return context.findElements(this);
    }

    protected static class ByRole extends PlaywrightiumBy implements Serializable {
        public ByRole(AriaRole ariaRole, ArialSearchOptions pageAriaRoleOptions) {
            super("getByRole", ariaRole, pageAriaRoleOptions);
        }

        public ByRole(AriaRole ariaRole) {
            this(ariaRole, null);
        }
    }

    protected static class ByAltText extends PlaywrightiumBy implements Serializable {
        public ByAltText(String value, Boolean exact) {
            super("getByAltText", value, exact);
        }

        public ByAltText(String value) {
            this(value, null);
        }
    }


    protected static class ByLabel extends PlaywrightiumBy implements Serializable {
        public ByLabel(String value, Boolean exact) {
            super("getByLabel", value, exact);
        }

        public ByLabel(String value) {
            this(value, null);
        }
    }

    protected static class ByPlaceholder extends PlaywrightiumBy implements Serializable {
        public ByPlaceholder(String value, Boolean exact) {
            super("getByPlaceholder", value, exact);
        }

        public ByPlaceholder(String value) {
            this(value, null);
        }
    }

    protected static class ByTestId extends PlaywrightiumBy implements Serializable {
        public ByTestId(String value) {
            super("getByTestId", value, null);
        }

    }

    protected static class ByText extends PlaywrightiumBy implements Serializable {
        public ByText(String value, Boolean exact) {
            super("getByText", value, exact);
        }

        public ByText(String value) {
            this(value, null);
        }
    }

    protected static class ByTitle extends PlaywrightiumBy implements Serializable {
        public ByTitle(String value, Boolean exact) {
            super("getByTitle", value, exact);
        }

        public ByTitle(String value) {
            this(value, null);
        }
    }

    public static By byRole(AriaRole ariaRole, ArialSearchOptions pageAriaRoleOptions) {
        return new ByRole(ariaRole, pageAriaRoleOptions);
    }

    public static By byRole(AriaRole ariaRole) {
        return new ByRole(ariaRole);
    }

    public static By byAltText(String text, Boolean exact) {
        return new ByAltText(text, exact);
    }

    public static By byAltText(String text) {
        return new ByAltText(text, null);
    }

    public static By byLabel(String text, Boolean exact) {
        return new ByLabel(text, exact);
    }

    public static By byLabel(String text) {
        return new ByLabel(text, null);
    }

    public static By byPlaceholder(String text, Boolean exact) {
        return new ByPlaceholder(text, exact);
    }

    public static By byPlaceholder(String text) {
        return new ByPlaceholder(text, null);
    }

    public static By byTestId(String text) {
        return new ByTestId(text);
    }

    public static By byText(String text, Boolean exact) {
        return new ByText(text, exact);
    }

    public static By byText(String text) {
        return new ByText(text, null);
    }


    public static By byTitle(String text, Boolean exact) {
        return new ByTitle(text, exact);
    }

    public static By byTitle(String text) {
        return new ByTitle(text, null);
    }


}
