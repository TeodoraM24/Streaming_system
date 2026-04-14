package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ListsDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lists") // Matches the 'lists' table name in your updated SQL
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
            joinColumns = @JoinColumn(name = "lists_list_id"), // Matches SQL: lists_list_id
            inverseJoinColumns = @JoinColumn(name = "content_content_id") // Matches SQL: content_content_id
    )
    private List<Content> contents = new ArrayList<>();

    public Lists(ListsDTO dto) {
        this.listId = dto.getListId();
        this.listname = dto.getListname();
    }
}