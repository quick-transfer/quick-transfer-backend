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
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User{

    public Admin(String name, String username, String email, String password) {
        super(name, username, email, password, Role.ADMIN);
    }
}
