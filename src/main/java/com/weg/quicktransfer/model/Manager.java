package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;

import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.Section;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "managers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Manager extends User{

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Section section;

    @OneToMany(mappedBy = "manager")
    List<Interview> interviews = new ArrayList<>();

    public Manager(String name, String userName, String email, String password, Section section) {
        super(name, userName, email, password, Role.MANAGER);
        this.section = section;
    }
}