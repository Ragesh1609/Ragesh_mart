package com.ragesh.rageshmart.dto;

import java.math.BigDecimal;

/**
 * Request shape for creating/editing a product listing (F2).
 * Uses the Builder pattern, as required by Section 12 for complex DTO construction,
 * and is also the target Gson deserializes POST/PUT bodies into.
 */
public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQty;
    private String category;
    private String imageUrl;

    public ProductRequestDTO() { } // required for Gson deserialization

    private ProductRequestDTO(Builder b) {
        this.name = b.name;
        this.description = b.description;
        this.price = b.price;
        this.stockQty = b.stockQty;
        this.category = b.category;
        this.imageUrl = b.imageUrl;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getStockQty() { return stockQty; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQty;
        private String category;
        private String imageUrl;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder stockQty(Integer stockQty) { this.stockQty = stockQty; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public ProductRequestDTO build() { return new ProductRequestDTO(this); }
    }
}
