package com.example.chapter7.util;

import com.example.chapter7.domain.CustomerEntity;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CustomerItemReader implements ItemReader<CustomerEntity> {

    private List<CustomerEntity> customers;
    private int curIndex;

    private String[] firstNames = {"Michael", "Warren", "Ann", "Terrence",
            "Erica", "Laura", "Steve", "Larry"};
    private String middleInitial = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String[] lastNames = {"Gates", "Darrow", "Donnelly", "Jobs",
            "Buffett", "Ellison", "Obama"};
    private String[] streets = {"4th Street", "Wall Street", "Fifth Avenue",
            "Mt. Lee Drive", "Jeopardy Lane",
            "Infinite Loop Drive", "Farnam Street",
            "Isabella Ave", "S. Greenwood Ave"};
    private String[] cities = {"Chicago", "New York", "Hollywood", "Aurora",
            "Omaha", "Atherton"};
    private String[] states = {"IL", "NY", "CA", "NE"};

    private Random generator = new Random();

    public CustomerItemReader() {
        curIndex = 0;

        customers = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            customers.add(buildCustomer());
        }
    }

    private CustomerEntity buildCustomer() {
        CustomerEntity customer = new CustomerEntity();

        customer.setId((long) generator.nextInt(Integer.MAX_VALUE));
        customer.setFirstName(
                firstNames[generator.nextInt(firstNames.length - 1)]);
        customer.setMiddleInitial(
                String.valueOf(middleInitial.charAt(
                        generator.nextInt(middleInitial.length() - 1))));
        customer.setLastName(
                lastNames[generator.nextInt(lastNames.length - 1)]);
        customer.setAddress(generator.nextInt(9999) + " " +
                streets[generator.nextInt(streets.length - 1)]);
        customer.setCity(cities[generator.nextInt(cities.length - 1)]);
        customer.setState(states[generator.nextInt(states.length - 1)]);
        customer.setZipCode(String.valueOf(generator.nextInt(99999)));

        return customer;
    }

    @Override
    public CustomerEntity read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        CustomerEntity customer = null;

        if (curIndex < customers.size()) {
            customer = customers.get(curIndex);
            curIndex++;
        }

        return customer;
    }
}
