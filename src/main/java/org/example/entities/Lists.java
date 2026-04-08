package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ListsDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "list")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private Long listId;

    @Column(name = "listname", nullable = false)
    private String listname;

    @ManyToOne
    @JoinColumn(name = "profile_profile_id")
    private Profile profile;

    @ManyToMany
    @JoinTable(
            name = "content_has_list",
            joinColumns = @JoinColumn(name = "list_list_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "content_content_id",
                    columnDefinition = "INT" // This tells Hibernate: "I know Java uses Long, but the DB is INT"
            )
    )
    private List<Content> contents;

    // Conversion Constructor
    public Lists(ListsDTO dto) {
        this.listId = dto.getListId();
        this.listname = dto.getListname();
    }
}