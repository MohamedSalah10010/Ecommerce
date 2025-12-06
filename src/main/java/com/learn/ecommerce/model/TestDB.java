package com.learn.ecommerce.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestDB {

    @Id
    private long id;
    @Column
    private String name;

}
