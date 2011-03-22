package com.magnetstreet.swt.example.persistence;

import com.magnetstreet.swt.example.bean.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * CustomerRecordDataDefaultBuilder
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
public class CustomerRecordDataDefaultBuilder {

    public static void buildDefaultDatabase(CustomerRecordData db) {
        createStates(db);

        createCustomer1(db);
        createCustomer2(db);
    }

    private static void createCustomer1(CustomerRecordData db) {
        CustomerRecord c1 = new CustomerRecord();
        c1.setId(1);
        c1.setName("Johnny Appleseed(1)");
        { // Customer 1 details
            Address c1Address = new Address();
            c1Address.setCustomer(c1);
            c1Address.setId(1);
            c1Address.setLineOne("123 main street");
            c1Address.setState(db.getStateByCode("WI"));
            c1Address.setZipcode("51245");
            c1.setAddress(c1Address);
            db.persist(c1Address);

            Order c1Order1 = new Order();
            c1Order1.setId(1);
            c1Order1.setCustomer(c1);
            c1Order1.setPaid(true);
            { // Customer 1 Order 1 Details
                OrderItem o1Item1 = new OrderItem();
                o1Item1.setId(1);
                o1Item1.setOrder(c1Order1);
                o1Item1.setProductName("Blue Ribbions(1)");
                o1Item1.setProductUnitPrice(new BigDecimal(.15));
                o1Item1.setQuantity(125);

                OrderItem o1Item2 = new OrderItem();
                o1Item2.setId(2);
                o1Item2.setOrder(c1Order1);
                o1Item2.setProductName("Blue Stickers(2)");
                o1Item2.setProductUnitPrice(new BigDecimal(.25));
                o1Item2.setQuantity(100);

                List<OrderItem> items = new ArrayList<OrderItem>();
                items.add(o1Item1);
                db.persist(o1Item1);
                items.add(o1Item2);
                db.persist(o1Item2);
                c1Order1.setItems(items);
            }
            db.persist(c1Order1);
            Order c1Order2 = new Order();
            c1Order2.setId(2);
            c1Order2.setCustomer(c1);
            c1Order2.setPaid(false);
            { // Customer 1 Order 1 Details
                OrderItem o2Item1 = new OrderItem();
                o2Item1.setId(3);
                o2Item1.setOrder(c1Order1);
                o2Item1.setProductName("Blue Ribbions(3)");
                o2Item1.setProductUnitPrice(new BigDecimal(.15));
                o2Item1.setQuantity(125);

                OrderItem o2Item2 = new OrderItem();
                o2Item2.setId(4);
                o2Item2.setOrder(c1Order1);
                o2Item2.setProductName("Blue Stickers(4)");
                o2Item2.setProductUnitPrice(new BigDecimal(.25));
                o2Item2.setQuantity(100);

                List<OrderItem> items = new ArrayList<OrderItem>();
                items.add(o2Item1);
                db.persist(o2Item1);
                items.add(o2Item2);
                db.persist(o2Item2);
                c1Order2.setItems(items);
            }
            db.persist(c1Order2);

            List<Order> orders = new ArrayList<Order>();
            orders.add(c1Order1);
            orders.add(c1Order2);
            c1.setOrders(orders);
        }
        db.persist(c1);
    }

    private static void createCustomer2(CustomerRecordData db) {
        CustomerRecord c1 = new CustomerRecord();
        c1.setId(2);
        c1.setName("Jimmy Huffa(2)");
        { // Customer 2 details
            Address c1Address = new Address();
            c1Address.setCustomer(c1);
            c1Address.setId(2);
            c1Address.setLineOne("14322 County V v");
            c1Address.setState(db.getStateByCode("MN"));
            c1Address.setZipcode("31245");
            c1.setAddress(c1Address);
            db.persist(c1Address);

            Order c1Order1 = new Order();
            c1Order1.setId(3);
            c1Order1.setCustomer(c1);
            c1Order1.setPaid(true);
            { // Customer 1 Order 1 Details
                OrderItem o1Item1 = new OrderItem();
                o1Item1.setId(5);
                o1Item1.setOrder(c1Order1);
                o1Item1.setProductName("Red Ribbions(5)");
                o1Item1.setProductUnitPrice(new BigDecimal(.17));
                o1Item1.setQuantity(125);

                OrderItem o1Item2 = new OrderItem();
                o1Item2.setId(6);
                o1Item2.setOrder(c1Order1);
                o1Item2.setProductName("Red Stickers(6)");
                o1Item2.setProductUnitPrice(new BigDecimal(.27));
                o1Item2.setQuantity(100);

                List<OrderItem> items = new ArrayList<OrderItem>();
                items.add(o1Item1);
                db.persist(o1Item1);
                items.add(o1Item2);
                db.persist(o1Item2);
                c1Order1.setItems(items);
            }
            db.persist(c1Order1);
            Order c1Order2 = new Order();
            c1Order2.setId(4);
            c1Order2.setCustomer(c1);
            c1Order2.setPaid(false);
            { // Customer 1 Order 1 Details
                OrderItem o2Item1 = new OrderItem();
                o2Item1.setId(7);
                o2Item1.setOrder(c1Order1);
                o2Item1.setProductName("GREEN Ribbions(7)");
                o2Item1.setProductUnitPrice(new BigDecimal(.11));
                o2Item1.setQuantity(225);

                OrderItem o2Item2 = new OrderItem();
                o2Item2.setId(8);
                o2Item2.setOrder(c1Order1);
                o2Item2.setProductName("Green Stickers(8)");
                o2Item2.setProductUnitPrice(new BigDecimal(.22));
                o2Item2.setQuantity(500);

                List<OrderItem> items = new ArrayList<OrderItem>();
                items.add(o2Item1);
                db.persist(o2Item1);
                items.add(o2Item2);
                db.persist(o2Item2);
                c1Order2.setItems(items);
            }
            db.persist(c1Order2);

            List<Order> orders = new ArrayList<Order>();
            orders.add(c1Order1);
            orders.add(c1Order2);
            c1.setOrders(orders);
        }
        db.persist(c1);
    }

    private static void createStates(CustomerRecordData db) {
        State s1 = new State();
        s1.setStateCode("WI");
        s1.setStateName("Wisconsin");
        s1.setTaxRate(new BigDecimal(5.5));

        State s2 = new State();
        s2.setStateCode("MN");
        s2.setStateName("Minnesota");
        s2.setTaxRate(new BigDecimal(6.5));

        State s3 = new State();
        s3.setStateCode("TX");
        s3.setStateName("Texas");
        s3.setTaxRate(new BigDecimal(0));

        db.persist(s1);
        db.persist(s2);
        db.persist(s3);
    }
}
