package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.EpisodeDTO;

import java.time.LocalDate;

@Entity
@Table(name = "episode")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    private Long episodeId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate releasedate;
    private Short duration;

    @ManyToOne
    @JoinColumn(name = "season_season_id")
    private Season season;

    public Episode(EpisodeDTO dto) {
        this.episodeId = dto.getEpisodeId();
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.releasedate = dto.getReleasedate();
        this.duration = dto.getDuration();
    }
}