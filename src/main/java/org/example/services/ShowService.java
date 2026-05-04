package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.ShowResponseDTO;
import org.example.entities.Content;
import org.example.repositories.ShowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;

    public List<ShowResponseDTO> getTop10RatedShows() {

        return showRepository.findTop10ByOrderByContent_RatingDesc()
                .stream()
                .map(s -> {
                    Content c = s.getContent();

                    ShowResponseDTO dto = new ShowResponseDTO();
                    dto.setShowsId(s.getShowsId());

                    dto.setTitle(c.getTitle());
                    dto.setRating(c.getRating());
                    dto.setDescription(c.getDescription());

                    return dto;
                })
                .toList();
    }
}
