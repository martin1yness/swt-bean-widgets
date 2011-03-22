package com.magnetstreet.swt.example.bean;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;

import java.util.List;

/**
 * Customer Record
 *
 * Simple example bean used in demonstrations of the hibernate-swt library.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
@SWTEntity(defaultCollectionType = SWTEntity.Type.DATA_GRID)
public class CustomerRecord {
    @SWTWidget(labelText = "ID:", readOnly = true)
    private Integer id;

    @SWTWidget(labelText = "Name:")
    private String name;

    @SWTWidget(labelText = "Address:")
    private Address address;

    @SWTWidget(labelText = "Order(s):")
    private List<Order> orders;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
