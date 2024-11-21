package org.brit.driver.adapters;

import com.codeborne.selenide.impl.WebElementSource;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Page;
import org.brit.element.converters.ElementHandleConverter;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

public class JsExecutionAdapter {
  private static final ElementHandleConverter converter = new ElementHandleConverter();

  /**
   * Executes a JavaScript script on a Playwright page.
   *
   * @param page   The Playwright page object where the script will be executed.
   * @param script The JavaScript code to be executed.
   * @param args   The arguments to pass to the script.
   * @return The result of executing the script.
   */
  public Object executeScript(Page page, String script, Object... args) {
    String modifiedScript = removeReturnKeyword(script);

    var arguments = args.length > 0 ? transformArguments(args) : List.of();
    JSHandle jsHandle = page.evaluateHandle("(arguments) => " + modifiedScript, arguments);
    String type = jsHandle.evaluate(
        """
            (node) => {
                if (node instanceof HTMLCollection) return 'HTMLCollection';
                if (node instanceof HTMLElement) return 'HTMLElement';
                if (Array.isArray(node)) return 'Array';
                return 'Other';
            }
            """).toString();

    switch (type) {
      case "HTMLCollection":
        return processHtmlCollection(page, jsHandle);
      case "HTMLElement":
        return converter.toPwElement(page, jsHandle.asElement());
      case "Array":
        return processArray(page, jsHandle);
      default:
        return jsHandle.jsonValue();
    }
  }

  /**
   * Executes an asynchronous JavaScript script on a Playwright page.
   *
   * @param page   The Playwright page object where the script will be executed.
   * @param script The JavaScript code to be executed.
   * @param args   The arguments to pass to the script.
   * @return A promise of the result of executing the asynchronous script.
   */
  public Object executeAsyncScript(Page page, String script, Object... args) {
    String modifiedScript = removeReturnKeyword(script);
    if (args.length > 0) {
      return page.evaluate("async () => {%s}".formatted(modifiedScript), transformArguments(args));
    } else {
      return page.evaluate("async () => {%s}".formatted(modifiedScript));
    }
  }

  private String removeReturnKeyword(String script) {
    return script.replaceFirst("^return", "").trim();
  }

  private List<PlaywrightWebElement> processHtmlCollection(Page page, JSHandle jsHandle) {
    int length = (int) page.evaluate("node => node.length", jsHandle);
    return IntStream.range(0, length)
        .mapToObj(i -> converter.toPwElement(page, page.evaluateHandle("node => node.item(%s)".formatted(i), jsHandle).asElement()))
        .toList();
  }

  private List<String> processArray(Page page, JSHandle jsHandle) {
    int length = (int) page.evaluate("node => node.length", jsHandle);
    return IntStream.range(0, length)
        .mapToObj(i -> Optional.ofNullable(page.evaluate("node => node['%s']".formatted(i), jsHandle)).map(Object::toString).orElse(null))
        .toList();
  }

  private List<Object> transformArguments(Object... args) {
    return Arrays.stream(args)
        .map(this::transformArgument)
        .toList();
  }

  private final Map<Class<?>, Function<Object, Object>> argumentTransformers = Map.of(
      WebElementSource.class, arg -> getElementHandleFrom((WebElementSource) arg),
      WrapsElement.class, arg -> getElementHandleFrom((WrapsElement) arg),
      WebElement.class, arg -> getElementHandleFrom((WebElement) arg),
      Collection.class, arg -> transformCollection((Collection<?>) arg)
  );

  private Object transformArgument(Object arg) {
    if (arg instanceof WebElementSource) {
      return getElementHandleFrom((WebElementSource) arg);
    } else if (arg instanceof WrapsElement) {
      return getElementHandleFrom((WrapsElement) arg);
    } else if (arg instanceof WebElement) {
      return getElementHandleFrom((WebElement) arg);
    } else if (arg instanceof Collection) {
      return transformCollection((Collection<?>) arg);
    }
    return arg;
  }

  private Object transformCollection(Collection<?> collection) {
    if (collection.isEmpty()) return List.of();

    if (collection.iterator().next() instanceof WebElement) {
      return collection.stream()
          .map(e -> ((PlaywrightWebElement) e).getLocator().elementHandle())
          .toList();
    }
    return List.copyOf(collection);
  }

  private ElementHandle getElementHandleFrom(WebElementSource source) {
    return ((PlaywrightWebElement) source.getWebElement()).getLocator().elementHandle();
  }

  private ElementHandle getElementHandleFrom(WrapsElement wrapsElement) {
    return ((PlaywrightWebElement) wrapsElement.getWrappedElement()).getElementHandle();
  }

  private ElementHandle getElementHandleFrom(WebElement webElement) {
    return ((PlaywrightWebElement) webElement).getLocator().elementHandle();
  }
}
