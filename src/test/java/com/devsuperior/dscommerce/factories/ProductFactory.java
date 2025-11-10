package com.devsuperior.dscommerce.factories;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {

    public static Product createProductWithDefaultParameters() {
        return new Product(1L, "iPhone 17", "6.3″ display, Apple A19 chipset, 3692 mAh battery, 512 GB storage, 8 GB RAM, Ceramic Shield 2.", 1200.0, "https://www.tomsguide.com/phones/iphones/ive-been-using-the-iphone-17-for-over-a-month-heres-what-i-really-think-about-it");
    }

    public static Product createProductWithParameters(String name, String description, Double price, String imgUrl) {
        return new Product(1L, name, description, price, imgUrl);
    }

    public static Product createProductCategory() {
        Product product = new Product(1L, "iPhone 17", "6.3″ display, Apple A19 chipset, 3692 mAh battery, 512 GB storage, 8 GB RAM, Ceramic Shield 2.", 1200.0, "https://www.tomsguide.com/phones/iphones/ive-been-using-the-iphone-17-for-over-a-month-heres-what-i-really-think-about-it");
        Category category = CategoryFactory.createCategory();
        product.getCategories().add(category);

        return product;
    }
}
