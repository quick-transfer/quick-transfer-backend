package com.weg.quicktransfer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.weg.quicktransfer.enums.Role;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coordinators")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@PrimaryKeyJoinColumn(name = "user_id")
public class Coordinator extends User{

    @OneToMany(mappedBy = "coordinator", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Course> courses;

    public Coordinator(String name, String username, String email, String password) {
        super(name, username, email, password, Role.COORDINATOR);
    }

}
