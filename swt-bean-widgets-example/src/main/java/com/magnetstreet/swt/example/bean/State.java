package com.magnetstreet.swt.example.bean;

import com.magnetstreet.swt.annotation.SWTWidget;
import com.magnetstreet.swt.annotation.SWTEntity;

import java.math.BigDecimal;

/**
 * State
 *
 * An auxillary POJO bean for representing a state on an address
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
@SWTEntity
public class State {
    @SWTWidget
    private String stateCode;

    private String stateName;
    private BigDecimal taxRate;

    public String getStateCode() {
        return stateCode;
    }
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
    public String getStateName() {
        return stateName;
    }
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
}
