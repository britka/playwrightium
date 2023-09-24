package org.brit.pages;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;

public class ProductsPage extends BasePage {

    public List<ProductModel> getProductsList() {
        return $$(".product-item").stream().map(element -> {
            String productName = element.$(".product-title > a").text();
            Long productPrice = Long.parseLong(element
                    .$(".add-info > .prices > .price.actual-price")
                    .text()
                    .replaceAll("[^\\d+]", ""));
            return new ProductModel(productName, productPrice);
        }).collect(Collectors.toList());
    }

    public ProductsPage sortBy(SortBy sortBy) {
        $("#products-orderby").selectOption(sortBy.optionValue());
        sleep(2000);
        return this;
    }

    public static void main(String[] args) {
        String price = "$1,800.00";

        System.out.println(price.replaceAll("[^\\d+]", ""));
    }
}
