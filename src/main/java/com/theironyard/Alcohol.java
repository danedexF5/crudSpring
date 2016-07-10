package com.theironyard;

import javax.persistence.*;

@Entity
public class Alcohol {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    int id;

    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String typeDrink;
    @Column(nullable = false)
    public Integer calories;

    @ManyToOne
    public User user;

    @Transient
    public boolean editable;
}