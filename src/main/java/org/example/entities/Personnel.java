package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.PersonnelDTO;
import org.example.enums.PersonnelRole;

@Entity
@Table(name = "personnel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personnelId;

    private String name;

    @Enumerated(EnumType.STRING)
    private PersonnelRole roletype;

    @ManyToMany(mappedBy = "personnel")
    @JsonIgnore // Prevents infinite loop: Personnel -> Content -> Personnel...
    private List<Content> contents;

    public Personnel(PersonnelDTO dto) {
        this.personnelId = dto.getPersonnelId();
        this.name = dto.getName();
        this.roletype = dto.getRoletype();
    }
}