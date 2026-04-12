package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.PersonnelDTO;
import org.example.enums.PersonnelRole;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "personnel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personnel_id")
    private Long personnelId;

    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "personnel_role")
    private PersonnelRole roletype;

    @ManyToMany(mappedBy = "personnel")
    @JsonIgnore
    private List<Content> contents;

    public Personnel(PersonnelDTO dto) {
        this.name = dto.getName();
        this.roletype = dto.getRoletype();
    }
}