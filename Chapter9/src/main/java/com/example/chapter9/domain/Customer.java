package com.example.chapter9.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Customer {
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String middleInitial;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;

}
