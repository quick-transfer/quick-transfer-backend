package com.weg.quicktransfer.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coordinators")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Coordinator extends User{

    @OneToMany(mappedBy = "coordinator", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Course> courses = new ArrayList<>();

    public Coordinator(String name, String userName, String email, String password) {
        super(name, userName, email, password, Role.COORDINATOR);
    }

}
