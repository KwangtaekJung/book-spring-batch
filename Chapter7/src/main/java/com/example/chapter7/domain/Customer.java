package com.example.chapter7.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class Customer {
//    private Long id;

//    @Column(name = "firstName")
    private String firstName;
//    @Column(name = "middleInitial")
    private String middleInitial;
//    @Column(name = "lastName")
    private String lastName;
    //	private String addressNumber;
//	private String street;
    private String address;
    private String city;
    private String state;
    private String zipCode;

}
