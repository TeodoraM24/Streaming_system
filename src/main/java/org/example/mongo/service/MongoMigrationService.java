package org.example.mongo.service;

import lombok.RequiredArgsConstructor;
import org.example.entities.*;
import org.example.mongo.documents.*;
import org.example.mongo.repositories.*;
import org.example.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoMigrationService {

    // PostgreSQL repositories
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final GenreRepository genreRepository;
    private final PersonnelRepository personnelRepository;
    private final ContentRepository contentRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final ListsRepository listsRepository;
    private final ReviewRepository reviewRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;

    // MongoDB repositories
    private final AccountMongoRepository accountMongoRepository;
    private final UserMongoRepository userMongoRepository;
    private final ProfileMongoRepository profileMongoRepository;
    private final GenreMongoRepository genreMongoRepository;
    private final PersonnelMongoRepository personnelMongoRepository;
    private final ContentMongoRepository contentMongoRepository;
    private final MovieMongoRepository movieMongoRepository;
    private final ShowMongoRepository showMongoRepository;
    private final SeasonMongoRepository seasonMongoRepository;
    private final EpisodeMongoRepository episodeMongoRepository;
    private final ListsMongoRepository listsMongoRepository;
    private final ReviewMongoRepository reviewMongoRepository;
    private final PlanMongoRepository planMongoRepository;
    private final SubscriptionMongoRepository subscriptionMongoRepository;
    private final PaymentMethodMongoRepository paymentMethodMongoRepository;
    private final PaymentMongoRepository paymentMongoRepository;
    private final ReceiptMongoRepository receiptMongoRepository;

    @Transactional
    public void migrateAll() {
        clearMongo();

        migrateAccounts();
        migrateUsers();
        migrateProfiles();
        migrateGenres();
        migratePersonnel();
        migrateContents();
        migrateMovies();
        migrateShows();
        migrateSeasons();
        migrateEpisodes();
        migrateLists();
        migrateReviews();
        migratePlans();
        migrateSubscriptions();
        migratePaymentMethods();
        migratePayments();
        migrateReceipts();
    }

    private void clearMongo() {
        receiptMongoRepository.deleteAll();
        paymentMongoRepository.deleteAll();
        paymentMethodMongoRepository.deleteAll();
        subscriptionMongoRepository.deleteAll();
        planMongoRepository.deleteAll();
        reviewMongoRepository.deleteAll();
        listsMongoRepository.deleteAll();
        episodeMongoRepository.deleteAll();
        seasonMongoRepository.deleteAll();
        showMongoRepository.deleteAll();
        movieMongoRepository.deleteAll();
        contentMongoRepository.deleteAll();
        personnelMongoRepository.deleteAll();
        genreMongoRepository.deleteAll();
        profileMongoRepository.deleteAll();
        userMongoRepository.deleteAll();
        accountMongoRepository.deleteAll();
    }

    private void migrateAccounts() {
        for (Account account : accountRepository.findAll()) {
            accountMongoRepository.save(AccountDocument.builder()
                    .accountId(account.getAccountId())
                    .firstname(account.getFirstname())
                    .lastname(account.getLastname())
                    .phonenumber(account.getPhonenumber())
                    .mail(account.getMail())
                    .build());
        }
    }

    private void migrateUsers() {
        for (User user : userRepository.findAll()) {
            userMongoRepository.save(UserDocument.builder()
                    .usersId(user.getUsersId())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .accountId(user.getAccount() != null ? user.getAccount().getAccountId() : null)
                    .build());
        }
    }

    private void migrateProfiles() {
        for (Profile profile : profileRepository.findAll()) {
            profileMongoRepository.save(ProfileDocument.builder()
                    .profileId(profile.getProfileId())
                    .profilename(profile.getProfilename())
                    .accountId(profile.getAccount() != null ? profile.getAccount().getAccountId() : null)
                    .build());
        }
    }

    private void migrateGenres() {
        for (Genre genre : genreRepository.findAll()) {
            genreMongoRepository.save(GenreDocument.builder()
                    .genreId(genre.getGenreId())
                    .genrename(genre.getGenrename())
                    .build());
        }
    }

    private void migratePersonnel() {
        for (Personnel personnel : personnelRepository.findAll()) {
            personnelMongoRepository.save(PersonnelDocument.builder()
                    .personnelId(personnel.getPersonnelId())
                    .name(personnel.getName())
                    .roletype(personnel.getRoletype() != null ? personnel.getRoletype().name() : null)
                    .build());
        }
    }

    private void migrateContents() {
        for (Content content : contentRepository.findAll()) {
            contentMongoRepository.save(ContentDocument.builder()
                    .contentId(content.getContentId())
                    .originaltitle(content.getOriginaltitle())
                    .title(content.getTitle())
                    .description(content.getDescription())
                    .rating(content.getRating())
                    .releasedate(content.getReleasedate())
                    .thumbnail(content.getThumbnail())
                    .type(content.getType() != null ? content.getType().name() : null)
                    .genres(content.getGenres() != null
                            ? content.getGenres().stream().map(Genre::getGenrename).toList()
                            : Collections.emptyList())
                    .personnel(content.getPersonnel() != null
                            ? content.getPersonnel().stream().map(Personnel::getName).toList()
                            : Collections.emptyList())
                    .build());
        }
    }

    private void migrateMovies() {
        for (Movie movie : movieRepository.findAll()) {
            movieMongoRepository.save(MovieDocument.builder()
                    .movieId(movie.getMovieId())
                    .duration(movie.getDuration())
                    .contentId(movie.getContent() != null ? movie.getContent().getContentId() : null)
                    .title(movie.getContent() != null ? movie.getContent().getTitle() : null)
                    .build());
        }
    }

    private void migrateShows() {
        for (Show show : showRepository.findAll()) {
            showMongoRepository.save(ShowDocument.builder()
                    .showsId(show.getShowsId())
                    .contentId(show.getContent() != null ? show.getContent().getContentId() : null)
                    .title(show.getContent() != null ? show.getContent().getTitle() : null)
                    .build());
        }
    }

    private void migrateSeasons() {
        for (Season season : seasonRepository.findAll()) {
            seasonMongoRepository.save(SeasonDocument.builder()
                    .seasonId(season.getSeasonId())
                    .title(season.getTitle())
                    .releasedate(season.getReleasedate())
                    .showId(season.getShow() != null ? season.getShow().getShowsId() : null)
                    .build());
        }
    }

    private void migrateEpisodes() {
        for (Episode episode : episodeRepository.findAll()) {
            episodeMongoRepository.save(EpisodeDocument.builder()
                    .episodeId(episode.getEpisodeId())
                    .title(episode.getTitle())
                    .description(episode.getDescription())
                    .releasedate(episode.getReleasedate())
                    .duration(episode.getDuration())
                    .seasonId(episode.getSeason() != null ? episode.getSeason().getSeasonId() : null)
                    .build());
        }
    }

    private void migrateLists() {
        for (Lists list : listsRepository.findAll()) {
            List<Long> contentIds = list.getContents() != null
                    ? list.getContents().stream().map(Content::getContentId).toList()
                    : Collections.emptyList();

            listsMongoRepository.save(ListsDocument.builder()
                    .listId(list.getListId())
                    .listname(list.getListname())
                    .profileId(list.getProfile() != null ? list.getProfile().getProfileId() : null)
                    .contentIds(contentIds)
                    .build());
        }
    }

    private void migrateReviews() {
        for (Review review : reviewRepository.findAll()) {
            reviewMongoRepository.save(ReviewDocument.builder()
                    .reviewId(review.getReviewId())
                    .title(review.getTitle())
                    .rating(review.getRating())
                    .comment(review.getComment())
                    .createdAt(review.getCreatedAt())
                    .profileId(review.getProfile() != null ? review.getProfile().getProfileId() : null)
                    .contentId(review.getContent() != null ? review.getContent().getContentId() : null)
                    .build());
        }
    }

    private void migratePlans() {
        for (Plan plan : planRepository.findAll()) {
            planMongoRepository.save(PlanDocument.builder()
                    .planId(plan.getPlanId())
                    .name(plan.getName())
                    .description(plan.getDescription())
                    .price(plan.getPrice())
                    .currency(plan.getCurrency())
                    .active(plan.getActive())
                    .build());
        }
    }

    private void migrateSubscriptions() {
        for (Subscription subscription : subscriptionRepository.findAll()) {
            subscriptionMongoRepository.save(SubscriptionDocument.builder()
                    .subscriptionId(subscription.getSubscriptionId())
                    .startdate(subscription.getStartdate())
                    .enddate(subscription.getEnddate())
                    .nextBillDate(subscription.getNextBillDate())
                    .status(subscription.getStatus() != null ? subscription.getStatus().name() : null)
                    .accountId(subscription.getAccount() != null ? subscription.getAccount().getAccountId() : null)
                    .planId(subscription.getPlan() != null ? subscription.getPlan().getPlanId() : null)
                    .build());
        }
    }

    private void migratePaymentMethods() {
        for (PaymentMethod paymentMethod : paymentMethodRepository.findAll()) {
            paymentMethodMongoRepository.save(PaymentMethodDocument.builder()
                    .paymentmethodId(paymentMethod.getPaymentmethodId())
                    .cardNumber(paymentMethod.getCardNumber())
                    .expirationMonth(paymentMethod.getExpirationMonth())
                    .expirationYear(paymentMethod.getExpirationYear())
                    .cvc(paymentMethod.getCvc())
                    .type(paymentMethod.getType() != null ? paymentMethod.getType().name() : null)
                    .defaultPaymentmethod(paymentMethod.getDefaultPaymentmethod())
                    .accountId(paymentMethod.getAccount() != null ? paymentMethod.getAccount().getAccountId() : null)
                    .build());
        }
    }

    private void migratePayments() {
        for (Payment payment : paymentRepository.findAll()) {
            paymentMongoRepository.save(PaymentDocument.builder()
                    .paymentId(payment.getPaymentId())
                    .price(payment.getPrice())
                    .currency(payment.getCurrency())
                    .createdAt(payment.getCreatedAt())
                    .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                    .subscriptionId(payment.getSubscription() != null ? payment.getSubscription().getSubscriptionId() : null)
                    .paymentMethodId(payment.getPaymentMethod() != null ? payment.getPaymentMethod().getPaymentmethodId() : null)
                    .build());
        }
    }

    private void migrateReceipts() {
        for (Receipt receipt : receiptRepository.findAll()) {
            receiptMongoRepository.save(ReceiptDocument.builder()
                    .receiptId(receipt.getReceiptId())
                    .receiptNumber(receipt.getReceiptNumber())
                    .price(receipt.getPrice())
                    .paydate(receipt.getPaydate())
                    .paymentId(receipt.getPayment() != null ? receipt.getPayment().getPaymentId() : null)
                    .build());
        }
    }
}
