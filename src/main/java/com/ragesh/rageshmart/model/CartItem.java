package com.ragesh.rageshmart.model;

import java.math.BigDecimal;

public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;   // joined convenience field
    private BigDecimal unitPrice; // joined convenience field
    private int quantity;

    public CartItem() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getLineTotal() {
        return unitPrice == null ? BigDecimal.ZERO : unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
