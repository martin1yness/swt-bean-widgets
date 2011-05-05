package com.magnetstreet.swt.example.bean;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * Order
 *
 * Auxillary bean used to demonstrate the ability to handle dataview
 * collections and not just singular instances.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
@SWTEntity(defaultCollectionType = SWTEntity.Type.DATA_GRID)
public class Order {
    @SWTWidget(labelText = "ID:", readOnly = true)
    private Integer id;
    @SWTWidget(labelText = "Items:")
    private List<OrderItem> items;
    private Division division;
    @SWTWidget(labelText = "Discount:")
    private BigDecimal discountTotal;
    @SWTWidget(labelText = "Total:")
    private BigDecimal totalCost;
    @SWTWidget(labelText = "Payment Recieved:")
    private boolean paid;
    private Calendar placedOn;
    private CustomerRecord customer;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    public Division getDivision() {
        return division;
    }
    public void setDivision(Division division) {
        this.division = division;
    }
    public BigDecimal getDiscountTotal() {
        return discountTotal;
    }
    public void setDiscountTotal(BigDecimal discountTotal) {
        this.discountTotal = discountTotal;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    public boolean isPaid() {
        return paid;
    }
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    public Calendar getPlacedOn() {
        return placedOn;
    }
    public void setPlacedOn(Calendar placedOn) {
        this.placedOn = placedOn;
    }
    public CustomerRecord getCustomer() {
        return customer;
    }
    public void setCustomer(CustomerRecord customer) {
        this.customer = customer;
    }
}
