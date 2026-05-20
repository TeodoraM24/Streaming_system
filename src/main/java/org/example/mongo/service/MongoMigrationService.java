package org.example.mongo.service;

import lombok.RequiredArgsConstructor;
import org.example.entities.*;
import org.example.mongo.documents.*;
import org.example.mongo.embedded.*;
import org.example.mongo.repositories.*;
import org.example.repositories.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mongo.migrations.enabled", havingValue = "true")
public class MongoMigrationService {

    // PostgreSQL repositories
    private final AccountRepository accountRepository;
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

    // Mongo repositories - kun rigtige collections
    private final AccountMongoRepository accountMongoRepository;
    private final ContentMongoRepository contentMongoRepository;
    private final GenreMongoRepository genreMongoRepository;
    private final PersonnelMongoRepository personnelMongoRepository;
    private final PlanMongoRepository planMongoRepository;

    @Transactional
    public void migrateAll() {
        clearMongo();

        migrateAccounts();
        migrateContent();
        migrateGenres();
        migratePersonnel();
        migratePlans();
    }

    private void clearMongo() {
        accountMongoRepository.deleteAll();
        contentMongoRepository.deleteAll();
        genreMongoRepository.deleteAll();
        personnelMongoRepository.deleteAll();
        planMongoRepository.deleteAll();
    }

    private void migrateAccounts() {
        for (Account account : accountRepository.findAll()) {
            accountMongoRepository.save(AccountDocument.builder()
                    .accountId(account.getAccountId())
                    .firstname(account.getFirstname())
                    .lastname(account.getLastname())
                    .phonenumber(account.getPhonenumber())
                    .mail(account.getMail())
                    .users(account.getUser() != null
                            ? List.of(UserEmbedded.builder()
                            .usersId(account.getUser().getUsersId())
                            .username(account.getUser().getUsername())
                            .password(account.getUser().getPassword())
                            .accountId(account.getAccountId())
                            .build())
                            : Collections.emptyList())
                    .profiles(mapProfiles(account))
                    .subscriptions(mapSubscriptions(account))
                    .paymentMethods(mapPaymentMethods(account))
                    .build());
        }
    }

    private void migrateContent() {
        for (Content content : contentRepository.findAll()) {
            contentMongoRepository.save(ContentDocument.builder()
                    .contentId(content.getContentId())
                    .originalTitle(content.getOriginaltitle())
                    .title(content.getTitle())
                    .description(content.getDescription())
                    .rating(content.getRating())
                    .releaseDate(content.getReleasedate())
                    .thumbnail(content.getThumbnail())
                    .type(content.getType() != null ? content.getType().name() : null)
                    .movie(mapMovie(content))
                    .show(mapShow(content))
                    .genres(mapGenres(content))
                    .personnel(mapPersonnel(content))
                    .build());
        }
    }

    private List<ProfileEmbedded> mapProfiles(Account account) {
        return profileRepository.findAll().stream()
                .filter(profile -> profile.getAccount() != null &&
                        profile.getAccount().getAccountId().equals(account.getAccountId()))
                .map(profile -> ProfileEmbedded.builder()
                        .profileId(profile.getProfileId())
                        .profilename(profile.getProfilename())
                        .accountId(account.getAccountId())
                        .lists(mapLists(profile))
                        .reviews(mapReviews(profile))
                        .build())
                .toList();
    }

    private List<ListsEmbedded> mapLists(Profile profile) {
        return listsRepository.findAll().stream()
                .filter(list -> list.getProfile() != null &&
                        list.getProfile().getProfileId().equals(profile.getProfileId()))
                .map(list -> ListsEmbedded.builder()
                        .listId(list.getListId())
                        .listname(list.getListname())
                        .profileId(profile.getProfileId())
                        .contentIds(list.getContents() != null
                                ? list.getContents().stream().map(Content::getContentId).toList()
                                : Collections.emptyList())
                        .content(list.getContents() != null
                                ? list.getContents().stream().map(this::mapContentSmall).toList()
                                : Collections.emptyList())
                        .build())
                .toList();
    }

    private List<ReviewEmbedded> mapReviews(Profile profile) {
        return reviewRepository.findAll().stream()
                .filter(review -> review.getProfile() != null &&
                        review.getProfile().getProfileId().equals(profile.getProfileId()))
                .map(review -> ReviewEmbedded.builder()
                        .reviewId(review.getReviewId())
                        .title(review.getTitle())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .createdAt(review.getCreatedAt())
                        .profileId(profile.getProfileId())
                        .contentId(review.getContent() != null ? review.getContent().getContentId() : null)
                        .content(review.getContent() != null ? mapContentSmall(review.getContent()) : null)
                        .build())
                .toList();
    }

    private List<SubscriptionEmbedded> mapSubscriptions(Account account) {
        return subscriptionRepository.findAll().stream()
                .filter(subscription -> subscription.getAccount() != null &&
                        subscription.getAccount().getAccountId().equals(account.getAccountId()))
                .map(subscription -> SubscriptionEmbedded.builder()
                        .subscriptionId(subscription.getSubscriptionId())
                        .startdate(subscription.getStartdate())
                        .enddate(subscription.getEnddate())
                        .nextBillDate(subscription.getNextBillDate())
                        .status(subscription.getStatus() != null ? subscription.getStatus().name() : null)
                        .accountId(account.getAccountId())
                        .planId(subscription.getPlan() != null ? subscription.getPlan().getPlanId() : null)
                        .plan(subscription.getPlan() != null ? mapPlanEmbedded(subscription.getPlan()) : null)
                        .payments(mapPayments(subscription))
                        .build())
                .toList();
    }

    private List<PaymentEmbedded> mapPayments(Subscription subscription) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getSubscription() != null &&
                        payment.getSubscription().getSubscriptionId().equals(subscription.getSubscriptionId()))
                .map(payment -> PaymentEmbedded.builder()
                        .paymentId(payment.getPaymentId())
                        .price(payment.getPrice())
                        .currency(payment.getCurrency())
                        .createdAt(payment.getCreatedAt())
                        .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                        .subscriptionId(subscription.getSubscriptionId())
                        .paymentMethodId(payment.getPaymentMethod() != null ? payment.getPaymentMethod().getPaymentmethodId() : null)
                        .paymentMethod(payment.getPaymentMethod() != null ? mapPaymentMethod(payment.getPaymentMethod()) : null)
                        .receipt(mapReceipt(payment))
                        .build())
                .toList();
    }

    private List<PaymentMethodEmbedded> mapPaymentMethods(Account account) {
        return paymentMethodRepository.findAll().stream()
                .filter(paymentMethod -> paymentMethod.getAccount() != null &&
                        paymentMethod.getAccount().getAccountId().equals(account.getAccountId()))
                .map(this::mapPaymentMethod)
                .toList();
    }

    private PaymentMethodEmbedded mapPaymentMethod(PaymentMethod paymentMethod) {
        return PaymentMethodEmbedded.builder()
                .paymentmethodId(paymentMethod.getPaymentmethodId())
                .cardNumber(paymentMethod.getCardNumber())
                .expirationMonth(paymentMethod.getExpirationMonth())
                .expirationYear(paymentMethod.getExpirationYear())
                .cvc(paymentMethod.getCvc())
                .type(paymentMethod.getType() != null ? paymentMethod.getType().name() : null)
                .defaultPaymentmethod(paymentMethod.getDefaultPaymentmethod())
                .accountId(paymentMethod.getAccount() != null ? paymentMethod.getAccount().getAccountId() : null)
                .build();
    }

    private ReceiptEmbedded mapReceipt(Payment payment) {
        return receiptRepository.findAll().stream()
                .filter(receipt -> receipt.getPayment() != null &&
                        receipt.getPayment().getPaymentId().equals(payment.getPaymentId()))
                .findFirst()
                .map(receipt -> ReceiptEmbedded.builder()
                        .receiptId(receipt.getReceiptId())
                        .receiptNumber(receipt.getReceiptNumber())
                        .price(receipt.getPrice())
                        .paydate(receipt.getPaydate())
                        .paymentId(payment.getPaymentId())
                        .build())
                .orElse(null);
    }

    private MovieEmbedded mapMovie(Content content) {
        return movieRepository.findAll().stream()
                .filter(movie -> movie.getContent() != null &&
                        movie.getContent().getContentId().equals(content.getContentId()))
                .findFirst()
                .map(movie -> MovieEmbedded.builder()
                        .movieId(movie.getMovieId())
                        .duration(movie.getDuration())
                        .contentId(content.getContentId())
                        .title(content.getTitle())
                        .build())
                .orElse(null);
    }

    private ShowEmbedded mapShow(Content content) {
        return showRepository.findAll().stream()
                .filter(show -> show.getContent() != null &&
                        show.getContent().getContentId().equals(content.getContentId()))
                .findFirst()
                .map(show -> ShowEmbedded.builder()
                        .showsId(show.getShowsId())
                        .contentId(content.getContentId())
                        .title(content.getTitle())
                        .seasons(mapSeasons(show))
                        .build())
                .orElse(null);
    }

    private List<SeasonEmbedded> mapSeasons(Show show) {
        return seasonRepository.findAll().stream()
                .filter(season -> season.getShow() != null &&
                        season.getShow().getShowsId().equals(show.getShowsId()))
                .map(season -> SeasonEmbedded.builder()
                        .seasonId(season.getSeasonId())
                        .title(season.getTitle())
                        .releasedate(season.getReleasedate())
                        .showId(show.getShowsId())
                        .episodes(mapEpisodes(season))
                        .build())
                .toList();
    }

    private List<EpisodeEmbedded> mapEpisodes(Season season) {
        return episodeRepository.findAll().stream()
                .filter(episode -> episode.getSeason() != null &&
                        episode.getSeason().getSeasonId().equals(season.getSeasonId()))
                .map(episode -> EpisodeEmbedded.builder()
                        .episodeId(episode.getEpisodeId())
                        .title(episode.getTitle())
                        .description(episode.getDescription())
                        .releasedate(episode.getReleasedate())
                        .duration(episode.getDuration())
                        .seasonId(season.getSeasonId())
                        .build())
                .toList();
    }

    private ContentSmallEmbedded mapContentSmall(Content content) {
        return ContentSmallEmbedded.builder()
                .contentId(content.getContentId())
                .title(content.getTitle())
                .type(content.getType() != null ? content.getType().name() : null)
                .build();
    }

    private PlanEmbedded mapPlanEmbedded(Plan plan) {
        return PlanEmbedded.builder()
                .planId(plan.getPlanId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .currency(plan.getCurrency())
                .active(plan.getActive())
                .build();
    }

    private List<GenreEmbedded> mapGenres(Content content) {
        return content.getGenres() != null
                ? content.getGenres().stream()
                .map(genre -> GenreEmbedded.builder()
                        .genreId(genre.getGenreId())
                        .genrename(genre.getGenrename())
                        .build())
                .toList()
                : Collections.emptyList();
    }

    private List<PersonnelEmbedded> mapPersonnel(Content content) {
        return content.getPersonnel() != null
                ? content.getPersonnel().stream()
                .map(person -> PersonnelEmbedded.builder()
                        .personnelId(person.getPersonnelId())
                        .name(person.getName())
                        .roletype(person.getRoletype() != null ? person.getRoletype().name() : null)
                        .build())
                .toList()
                : Collections.emptyList();
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
}