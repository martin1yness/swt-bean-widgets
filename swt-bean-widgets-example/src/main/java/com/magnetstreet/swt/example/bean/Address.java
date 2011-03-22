package com.magnetstreet.swt.example.bean;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;

/**
 * Address
 *
 * Auxillary example POJO linked by CustomerRecord to give a
 * life like example.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
@SWTEntity(defaultCollectionType = SWTEntity.Type.DATA_GRID)
public class Address {
    private Integer id;
    @SWTWidget(labelText = "Address L1:")
    private String lineOne;
    @SWTWidget(labelText = "Address L2:")
    private String lineTwo;
    @SWTWidget(labelText = "State:")
    private State state;
    @SWTWidget(labelText = "Zip:")
    private String zipcode;
    private CustomerRecord customer;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLineOne() {
        return lineOne;
    }
    public void setLineOne(String lineOne) {
        this.lineOne = lineOne;
    }
    public String getLineTwo() {
        return lineTwo;
    }
    public void setLineTwo(String lineTwo) {
        this.lineTwo = lineTwo;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public String getZipcode() {
        return zipcode;
    }
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    public CustomerRecord getCustomer() {
        return customer;
    }
    public void setCustomer(CustomerRecord customer) {
        this.customer = customer;
    }
}
