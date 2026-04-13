package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ShowDTO;

import java.util.List;

@Entity
@Table(name = "shows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shows_id")
    private Long showsId;

    @OneToOne(optional = false)
    @JoinColumn(name = "content_content_id", nullable = false)
    private Content content;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons;


    public Show(ShowDTO dto) {
        this.showsId = dto.getShowsId();
    }
}