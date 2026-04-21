package org.example.neo4j.service;

import lombok.RequiredArgsConstructor;
import org.example.entities.Content;
import org.example.entities.Genre;
import org.example.entities.Movie;
import org.example.entities.Personnel;
import org.example.entities.Profile;
import org.example.entities.Review;
import org.example.entities.Show;
import org.example.entities.User;
import org.example.neo4j.nodes.GenreNode;
import org.example.neo4j.nodes.MovieNode;
import org.example.neo4j.nodes.PersonnelNode;
import org.example.neo4j.nodes.ProfileNode;
import org.example.neo4j.nodes.ReviewNode;
import org.example.neo4j.nodes.ShowNode;
import org.example.neo4j.nodes.UserNode;
import org.example.neo4j.repositories.GenreNeoRepository;
import org.example.neo4j.repositories.MovieNeoRepository;
import org.example.neo4j.repositories.PersonnelNeoRepository;
import org.example.neo4j.repositories.ProfileNeoRepository;
import org.example.neo4j.repositories.ReviewNeoRepository;
import org.example.neo4j.repositories.ShowNeoRepository;
import org.example.neo4j.repositories.UserNeoRepository;
import org.example.repositories.GenreRepository;
import org.example.repositories.MovieRepository;
import org.example.repositories.PersonnelRepository;
import org.example.repositories.ProfileRepository;
import org.example.repositories.ReviewRepository;
import org.example.repositories.ShowRepository;
import org.example.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MigrationService {

    // Postgres repositories
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final GenreRepository genreRepository;
    private final PersonnelRepository personnelRepository;
    private final ReviewRepository reviewRepository;

    // Neo4j repositories
    private final UserNeoRepository userNeoRepository;
    private final ProfileNeoRepository profileNeoRepository;
    private final MovieNeoRepository movieNeoRepository;
    private final ShowNeoRepository showNeoRepository;
    private final GenreNeoRepository genreNeoRepository;
    private final PersonnelNeoRepository personnelNeoRepository;
    private final ReviewNeoRepository reviewNeoRepository;

    @Transactional(readOnly = true)
    public void migrateAll() {
        migrateUsers();
        migrateProfiles();
        migrateGenres();
        migratePersonnel();
        migrateMovies();
        migrateShows();
        migrateReviews();

        migrateMovieGenreRelationships();
        migrateShowGenreRelationships();
        migrateMoviePersonnelRelationships();
        migrateShowPersonnelRelationships();
        migrateProfileReviewRelationships();
        migrateReviewContentRelationships();
    }

    @Transactional(readOnly = true)
    public void migrateUsers() {
        List<UserNode> nodes = userRepository.findAll().stream()
                .map(this::mapUserToNode)
                .toList();

        userNeoRepository.saveAll(nodes);
    }

    @Transactional(readOnly = true)
    public void migrateProfiles() {
        List<ProfileNode> nodes = profileRepository.findAll().stream()
                .map(this::mapProfileToNode)
                .toList();

        profileNeoRepository.saveAll(nodes);
    }

    @Transactional(readOnly = true)
    public void migrateGenres() {
        List<GenreNode> nodes = genreRepository.findAll().stream()
                .map(this::mapGenreToNode)
                .toList();

        genreNeoRepository.saveAll(nodes);
    }

    @Transactional(readOnly = true)
    public void migratePersonnel() {
        List<PersonnelNode> nodes = personnelRepository.findAll().stream()
                .map(this::mapPersonnelToNode)
                .toList();

        personnelNeoRepository.saveAll(nodes);
    }

    @Transactional(readOnly = true)
    public void migrateMovies() {
        List<MovieNode> nodes = movieRepository.findAll().stream()
                .map(this::mapMovieToNode)
                .toList();

        movieNeoRepository.saveAll(nodes);
    }

    @Transactional(readOnly = true)
    public void migrateShows() {
        List<ShowNode> nodes = showRepository.findAll().stream()
                .map(this::mapShowToNode)
                .toList();

        showNeoRepository.saveAll(nodes);
    }

    @Transactional(readOnly = true)
    public void migrateReviews() {
        List<ReviewNode> nodes = reviewRepository.findAll().stream()
                .map(this::mapReviewToNode)
                .toList();

        reviewNeoRepository.saveAll(nodes);
    }

    @Transactional(readOnly = true)
    public void migrateMovieGenreRelationships() {
        List<Movie> movies = movieRepository.findAll();

        for (Movie movie : movies) {
            MovieNode movieNode = movieNeoRepository.findById(movie.getMovieId()).orElse(null);
            if (movieNode == null) continue;

            Content content = movie.getContent();
            if (content == null || content.getGenres() == null) continue;

            for (Genre genre : content.getGenres()) {
                genreNeoRepository.findById(genre.getGenreId())
                        .ifPresent(movieNode.getGenres()::add);
            }

            movieNeoRepository.save(movieNode);
        }
    }

    @Transactional(readOnly = true)
    public void migrateShowGenreRelationships() {
        List<Show> shows = showRepository.findAll();

        for (Show show : shows) {
            ShowNode showNode = showNeoRepository.findById(show.getShowsId()).orElse(null);
            if (showNode == null) continue;

            Content content = show.getContent();
            if (content == null || content.getGenres() == null) continue;

            for (Genre genre : content.getGenres()) {
                genreNeoRepository.findById(genre.getGenreId())
                        .ifPresent(showNode.getGenres()::add);
            }

            showNeoRepository.save(showNode);
        }
    }

    @Transactional(readOnly = true)
    public void migrateMoviePersonnelRelationships() {
        List<Movie> movies = movieRepository.findAll();

        for (Movie movie : movies) {
            MovieNode movieNode = movieNeoRepository.findById(movie.getMovieId()).orElse(null);
            if (movieNode == null) continue;

            Content content = movie.getContent();
            if (content == null || content.getPersonnel() == null) continue;

            for (Personnel person : content.getPersonnel()) {
                personnelNeoRepository.findById(person.getPersonnelId())
                        .ifPresent(movieNode.getPersonnel()::add);
            }

            movieNeoRepository.save(movieNode);
        }
    }

    @Transactional(readOnly = true)
    public void migrateShowPersonnelRelationships() {
        List<Show> shows = showRepository.findAll();

        for (Show show : shows) {
            ShowNode showNode = showNeoRepository.findById(show.getShowsId()).orElse(null);
            if (showNode == null) continue;

            Content content = show.getContent();
            if (content == null || content.getPersonnel() == null) continue;

            for (Personnel person : content.getPersonnel()) {
                personnelNeoRepository.findById(person.getPersonnelId())
                        .ifPresent(showNode.getPersonnel()::add);
            }

            showNeoRepository.save(showNode);
        }
    }

    @Transactional(readOnly = true)
    public void migrateProfileReviewRelationships() {
        List<Review> reviews = reviewRepository.findAll();

        for (Review review : reviews) {
            if (review.getProfile() == null) continue;

            ProfileNode profileNode = profileNeoRepository.findById(review.getProfile().getProfileId()).orElse(null);
            ReviewNode reviewNode = reviewNeoRepository.findById(review.getReviewId()).orElse(null);

            if (profileNode == null || reviewNode == null) continue;

            profileNode.getReviews().add(reviewNode);
            profileNeoRepository.save(profileNode);
        }
    }

    @Transactional(readOnly = true)
    public void migrateReviewContentRelationships() {
        List<Review> reviews = reviewRepository.findAll();

        for (Review review : reviews) {
            ReviewNode reviewNode = reviewNeoRepository.findById(review.getReviewId()).orElse(null);
            if (reviewNode == null) continue;

            Content content = review.getContent();
            if (content == null) continue;

            if (content.getMovie() != null) {
                movieNeoRepository.findById(content.getMovie().getMovieId())
                        .ifPresent(reviewNode::setMovie);
            }

            if (content.getShow() != null) {
                showNeoRepository.findById(content.getShow().getShowsId())
                        .ifPresent(reviewNode::setShow);
            }

            reviewNeoRepository.save(reviewNode);
        }
    }

    private UserNode mapUserToNode(User user) {
        UserNode node = new UserNode();
        node.setId(user.getUsersId());
        node.setUsername(user.getUsername());
        return node;
    }

    private ProfileNode mapProfileToNode(Profile profile) {
        ProfileNode node = new ProfileNode();
        node.setId(profile.getProfileId());
        node.setProfilename(profile.getProfilename());
        return node;
    }

    private GenreNode mapGenreToNode(Genre genre) {
        GenreNode node = new GenreNode();
        node.setId(genre.getGenreId());
        node.setGenrename(genre.getGenrename());
        return node;
    }

    private PersonnelNode mapPersonnelToNode(Personnel personnel) {
        PersonnelNode node = new PersonnelNode();
        node.setId(personnel.getPersonnelId());
        node.setName(personnel.getName());
        node.setRoletype(personnel.getRoletype());
        return node;
    }

    private MovieNode mapMovieToNode(Movie movie) {
        MovieNode node = new MovieNode();
        node.setId(movie.getMovieId());
        node.setDuration(movie.getDuration());

        Content content = movie.getContent();
        if (content != null) {
            node.setOriginaltitle(content.getOriginaltitle());
            node.setTitle(content.getTitle());
            node.setDescription(content.getDescription());
            node.setRating(content.getRating());
            node.setReleasedate(content.getReleasedate());
            node.setThumbnail(content.getThumbnail());
            node.setType(content.getType());
        }

        return node;
    }

    private ShowNode mapShowToNode(Show show) {
        ShowNode node = new ShowNode();
        node.setId(show.getShowsId());

        Content content = show.getContent();
        if (content != null) {
            node.setOriginaltitle(content.getOriginaltitle());
            node.setTitle(content.getTitle());
            node.setDescription(content.getDescription());
            node.setRating(content.getRating());
            node.setReleasedate(content.getReleasedate());
            node.setThumbnail(content.getThumbnail());
            node.setType(content.getType());
        }

        return node;
    }

    private ReviewNode mapReviewToNode(Review review) {
        ReviewNode node = new ReviewNode();
        node.setId(review.getReviewId());
        node.setTitle(review.getTitle());
        node.setRating(review.getRating());
        node.setComment(review.getComment());
        node.setCreatedAt(review.getCreatedAt());
        return node;
    }
}