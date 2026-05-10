package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.ShowDetailsDTO;
import org.example.dtos.ShowResponseDTO;
import org.example.entities.Content;
import org.example.repositories.ShowRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;

    @Transactional(readOnly = true)
    public ShowDetailsDTO getShowDetails(Long id) {
        return showRepository.findWithSeasonsByShowsId(id)
                .map(ShowDetailsDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

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
