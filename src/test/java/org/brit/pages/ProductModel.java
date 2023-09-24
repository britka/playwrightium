package org.brit.pages;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProductModel {
    private String productName;
    private Long productPrice;

    public ProductModel(String productName, Long productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public String productName() {
        return productName;
    }

    public ProductModel setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Long productPrice() {
        return productPrice;
    }

    public ProductModel setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProductModel that = (ProductModel) o;

        return new EqualsBuilder().append(productName, that.productName).append(productPrice, that.productPrice).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(productName).append(productPrice).toHashCode();
    }
}
