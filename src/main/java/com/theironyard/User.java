package com.theironyard;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    int id;

    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String password;

    @OneToMany(mappedBy = "user")
    public List<Alcohol> drinks;
}
