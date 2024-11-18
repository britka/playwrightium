package org.brit.element.adapters;

import com.microsoft.playwright.Locator;

import java.util.Set;

public class GetAttributeAdapter {
  private static final Set<String> booleanAttributes = Set.of(
      "async", "autofocus", "autoplay", "checked", "compact", "complete", "controls", "declare",
      "defaultchecked", "defaultselected", "defer", "disabled", "draggable", "ended", "formnovalidate",
      "hidden", "indeterminate", "iscontenteditable", "ismap", "itemscope", "loop", "multiple",
      "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", "open", "paused",
      "pubdate", "readonly", "required", "reversed", "scoped", "seamless", "seeking", "selected",
      "truespeed", "willvalidate"
  );

  public static String getDomProperty(Locator locator,String name) {
    Object jsProp = locator.evaluate("node => node['%s']".formatted(name));
    return jsProp != null ? jsProp.toString() : null;
  }

  public static String getDomAttribute(Locator locator, String name) {
    Boolean isPresent = (Boolean) locator.evaluate("node => node.hasAttribute('%s')".formatted(name));

    if (booleanAttributes.contains(name)) {
      return isPresent ? "true" : null;
    }

    return isPresent
        ? String.valueOf(locator.evaluate("node => node.getAttributeNode('%s').value".formatted(name)))
        : null;
  }

  public static String getAttribute(Locator locator, String name) {
    // sometimes relative href attribute is returned without leading /
    if (name.equals("href")) {
      return String.valueOf(locator.evaluate("node => node.href"));
    }

    if (booleanAttributes.contains(name)) {
      Boolean isPresent = (Boolean) locator.evaluate("node => node.hasAttribute('%s')".formatted(name));
      return isPresent ? "true" : null;
    }

    String attributeValue = locator.getAttribute(name);
    if (attributeValue == null) {
      Object jsProp = locator.evaluate("node => node['%s']".formatted(name));
      return jsProp != null ? jsProp.toString() : null;
    }
    return attributeValue;
  }

}
