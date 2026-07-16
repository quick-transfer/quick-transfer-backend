package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;

import javax.management.relation.Role;

import com.weg.quicktransfer.enums.Section;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "managers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Manager extends User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Section section;

    @OneToMany(mappedBy = "manager")
    List<Interview> interviews = new ArrayList<>();

    public Manager(String name, String userName, String email, String password, Role role, Section section) {
        super(name, userName, email, password, role);
        this.section = section;
    }
}
