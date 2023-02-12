package com.example.chapter8.service;

import com.example.chapter8.domain.Customer;
import org.springframework.batch.item.ItemProcessor;

public class EvenFilteringItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        return Integer.parseInt(customer.getZip()) % 2 == 0 ? null : customer;
    }
}
