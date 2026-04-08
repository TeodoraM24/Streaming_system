package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.SeasonDTO;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "season")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seasonId;

    private String title;
    private LocalDate releasedate;

    @ManyToOne
    @JoinColumn(name = "shows_shows_id")
    private Show show;

    @OneToMany(mappedBy = "season")
    private List<Episode> episodes;

    // Conversion Constructor
    public Season(SeasonDTO dto) {
        this.seasonId = dto.getSeasonId();
        this.title = dto.getTitle();
        this.releasedate = dto.getReleasedate();
    }
}