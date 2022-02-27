package com.sougata.train_booking.models.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String userName;
    private String password;
    private String roles;
    private String name;

    private boolean active;

    public User(String userName, String password, String roles, String name, boolean active) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
        this.name = name;
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }

    public User() {
    }
}

