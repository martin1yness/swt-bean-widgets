package com.magnetstreet.swt.example.persistence;

import com.magnetstreet.swt.example.bean.*;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Customer Record Data
 *
 * Class is like a poor man's database for cutsomer record data, somewhat
 * mimics a Hibernate DAO
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
public class CustomerRecordData {
    private Set<CustomerRecord> customerRecordTable = new HashSet<CustomerRecord>();
    private Set<Address> addressTable = new HashSet<Address>();
    private Set<State> stateTable = new HashSet<State>();
    private Set<Order> orderTable = new HashSet<Order>();
    private Set<OrderItem> orderItemTable = new HashSet<OrderItem>();

    public CustomerRecordData() {
        CustomerRecordDataDefaultBuilder.buildDefaultDatabase(this);
    }

    public Object persist(Object bean) {
        if(bean instanceof CustomerRecord)
            customerRecordTable.add((CustomerRecord)bean);
        else if(bean instanceof Address)
            addressTable.add((Address)bean);
        else if(bean instanceof State)
            stateTable.add((State)bean);
        else if(bean instanceof Order)
            orderTable.add((Order)bean);
        else if(bean instanceof OrderItem)
            orderItemTable.add((OrderItem)bean);
        else
            throw new RuntimeException("Unknown entity bean type, cannot persist: "+ bean);
        return bean;
    }

    public List<CustomerRecord> getAllCustomers() {
        return new ArrayList<CustomerRecord>(customerRecordTable);
    }

    public State getStateByCode(String stateCode) {
        if(stateTable.size() < 1) return null;
        for(State s: stateTable) {
            if(s.getStateCode().equalsIgnoreCase(stateCode))
                return s;
        }
        return null;
    }
}
