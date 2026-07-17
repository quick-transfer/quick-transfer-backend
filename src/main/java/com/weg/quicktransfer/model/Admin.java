package com.weg.quicktransfer.model;

import com.weg.quicktransfer.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Admin extends User{

    public Admin(String name, String userName, String email, String password) {
        super(name, userName, email, password, Role.ADMIN);
    }
}
