package com.example.chapter7.mapper;

import com.example.chapter7.domain.CustomerDomain;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomerRowMapper implements RowMapper<CustomerDomain> {
    @Override
    public CustomerDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerDomain customer = new CustomerDomain();

        customer.setId(rs.getLong("id"));
        customer.setAddress(rs.getString("address"));
        customer.setCity(rs.getString("city"));
        customer.setFirstName(rs.getString("firstName"));
        customer.setLastName(rs.getString("lastName"));
        customer.setMiddleInitial(rs.getString("middleInitial"));
        customer.setState(rs.getString("state"));
        customer.setZipCode(rs.getString("zipCode"));

        return customer;
    }
}
