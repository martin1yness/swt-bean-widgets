package com.magnetstreet.swt.example.bean;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;

import java.math.BigDecimal;

/**
 * OrderItem
 *
 * Order auxillary item to show ability of collections within collections
 * and their representation.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
@SWTEntity(defaultCollectionType = SWTEntity.Type.DATA_GRID)
public class OrderItem {
    private Integer id;
    @SWTWidget(labelText = "Product:")
    private String productName;
    @SWTWidget(labelText = "Unit Price:")
    private BigDecimal productUnitPrice;
    @SWTWidget(labelText = "Quantity:")
    private Integer quantity;
    private Order order;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public BigDecimal getProductUnitPrice() {
        return productUnitPrice;
    }
    public void setProductUnitPrice(BigDecimal productUnitPrice) {
        this.productUnitPrice = productUnitPrice;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
}
