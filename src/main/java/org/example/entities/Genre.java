package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.GenreDTO;

import java.util.List;

@Entity
@Table(name = "genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    private String genrename;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore // Prevents infinite loop: Genre -> Content -> Genre...
    private List<Content> contents;

    public Genre(GenreDTO dto) {
        this.genreId = dto.getGenreId(); // Fixed: was getAccountId()
        this.genrename = dto.getGenrename();
    }
}