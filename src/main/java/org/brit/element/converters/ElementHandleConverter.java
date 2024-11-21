package org.brit.element.converters;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.brit.element.PlaywrightWebElement;

/***
 * Converts ElementHandle to PlaywrightWebElement
 */
public class ElementHandleConverter {

  public PlaywrightWebElement toPwElement(Page page, ElementHandle node) {
    //  JS code calculates a unique CSS selector path for the node.
    //  It does this by constructing a path from the node itself to the root of the document.
    String string = page.evaluate("""
                node =>
                {
                    names = [];                
                    do {
                        index = 0;
                        cursorElement = node;
                        while (cursorElement !== null) {
                            ++index;
                            cursorElement = cursorElement.previousElementSibling;
                        }
                        names.unshift(node.tagName + ":nth-child(" + index + ")");
                        node = node.parentElement;
                    } while (node !== null);
                                
                    return names.join(" > ");
                }
                """, node).toString();

    return new PlaywrightWebElement(page.locator(string));
  }
}
