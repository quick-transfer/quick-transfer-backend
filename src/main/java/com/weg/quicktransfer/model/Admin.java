package com.weg.quicktransfer.model;

import com.weg.quicktransfer.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admins")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Admin extends User{

    public Admin(String name, String userName, String email, String password) {
        super(name, userName, email, password, Role.ADMIN);
    }
}
