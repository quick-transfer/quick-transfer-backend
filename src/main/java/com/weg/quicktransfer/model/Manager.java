package com.weg.quicktransfer.model;

import java.util.List;

import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.Section;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "managers")
@DiscriminatorValue("MANAGER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@PrimaryKeyJoinColumn(name = "user_id")
public class Manager extends User{
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Section section;

    @OneToMany(mappedBy = "manager")
    List<Interview> interviews;

    public Manager(String name, String username, String email, String password, Section section) {
        super(name, username, email, password, Role.MANAGER);
        this.section = section;
    }
}