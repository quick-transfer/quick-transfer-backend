package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.enums.EntityStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "places")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, name = "place_name")
    private String placeName;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String city;

    @Column(length = 2)
    private String state;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.ACTIVE;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Park park;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Section section;

    @OneToMany(mappedBy = "place")
    private List<Vacancy> vacancies = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<Interview> interviews = new ArrayList<>();

    public Place(String placeName, Park park, Section section) {
        this.placeName = placeName;
        this.code = defaultCode(placeName);
        this.park = park;
        this.section = section;
        this.status = EntityStatus.ACTIVE;
    }

    public Place(String placeName, String code, String description, String city, String state,
            EntityStatus status, Park park, Section section) {
        this.placeName = placeName;
        this.code = code;
        this.description = description;
        this.city = city;
        this.state = state;
        this.status = status;
        this.park = park;
        this.section = section;
    }

    @PrePersist
    void applyDefaults() {
        if (code == null || code.isBlank()) {
            code = defaultCode(placeName);
        }
        if (status == null) {
            status = EntityStatus.ACTIVE;
        }
    }

    private String defaultCode(String value) {
        String normalized = value == null ? "PLACE" : value.trim().toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "-").replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "PLACE" : normalized;
    }
}
