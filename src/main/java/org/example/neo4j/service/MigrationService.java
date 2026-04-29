package org.example.neo4j.service;

import lombok.RequiredArgsConstructor;
import org.example.entities.*;
import org.example.neo4j.nodes.*;
import org.example.neo4j.repositories.*;
import org.example.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MigrationService {

    // Postgres repositories
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ContentRepository contentRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final GenreRepository genreRepository;
    private final PersonnelRepository personnelRepository;
    private final ReviewRepository reviewRepository;
    private final ListsRepository listsRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ReceiptRepository receiptRepository;

    // Neo4j repositories
    private final AccountNeoRepository accountNeoRepository;
    private final UserNeoRepository userNeoRepository;
    private final ProfileNeoRepository profileNeoRepository;
    private final ContentNeoRepository contentNeoRepository;
    private final MovieNeoRepository movieNeoRepository;
    private final ShowNeoRepository showNeoRepository;
    private final SeasonNeoRepository seasonNeoRepository;
    private final EpisodeNeoRepository episodeNeoRepository;
    private final GenreNeoRepository genreNeoRepository;
    private final PersonnelNeoRepository personnelNeoRepository;
    private final ReviewNeoRepository reviewNeoRepository;
    private final ListsNeoRepository listsNeoRepository;
    private final SubscriptionNeoRepository subscriptionNeoRepository;
    private final PlanNeoRepository planNeoRepository;
    private final PaymentNeoRepository paymentNeoRepository;
    private final PaymentMethodNeoRepository paymentMethodNeoRepository;
    private final ReceiptNeoRepository receiptNeoRepository;

    @Transactional
    public void migrateAll() {
        migrateAccounts();
        migrateUsers();
        migrateProfiles();
        migrateContents();
        migrateGenres();
        migratePersonnel();
        migrateMovies();
        migrateShows();
        migrateSeasons();
        migrateEpisodes();
        migrateReviews();
        migrateLists();
        migratePlans();
        migratePaymentMethods();
        migrateReceipts();
        migratePayments();
        migrateSubscriptions();

        migrateAccountUserRelationships();
        migrateUserProfileRelationships();
        migrateProfileReviewRelationships();
        migrateProfileListRelationships();
        migrateListContentRelationships();
        migrateReviewContentRelationships();

        migrateMovieGenreRelationships();
        migrateShowGenreRelationships();
        migrateMoviePersonnelRelationships();
        migrateShowPersonnelRelationships();

        migrateShowSeasonRelationships();
        migrateSeasonEpisodeRelationships();

        migrateSubscriptionPlanRelationships();
        migrateSubscriptionPaymentRelationships();
        migratePaymentMethodRelationships();
        migratePaymentReceiptRelationships();
        migrateAccountSubscriptionRelationships();
    }

    public void migrateAccounts() {
        accountNeoRepository.saveAll(accountRepository.findAll().stream().map(this::mapAccountToNode).toList());
    }

    public void migrateUsers() {
        userNeoRepository.saveAll(userRepository.findAll().stream().map(this::mapUserToNode).toList());
    }

    public void migrateProfiles() {
        profileNeoRepository.saveAll(profileRepository.findAll().stream().map(this::mapProfileToNode).toList());
    }

    public void migrateContents() {
        contentNeoRepository.saveAll(contentRepository.findAll().stream().map(this::mapContentToNode).toList());
    }

    public void migrateGenres() {
        genreNeoRepository.saveAll(genreRepository.findAll().stream().map(this::mapGenreToNode).toList());
    }

    public void migratePersonnel() {
        personnelNeoRepository.saveAll(personnelRepository.findAll().stream().map(this::mapPersonnelToNode).toList());
    }

    public void migrateMovies() {
        movieNeoRepository.saveAll(movieRepository.findAll().stream().map(this::mapMovieToNode).toList());
    }

    public void migrateShows() {
        showNeoRepository.saveAll(showRepository.findAll().stream().map(this::mapShowToNode).toList());
    }

    public void migrateSeasons() {
        seasonNeoRepository.saveAll(seasonRepository.findAll().stream().map(this::mapSeasonToNode).toList());
    }

    public void migrateEpisodes() {
        episodeNeoRepository.saveAll(episodeRepository.findAll().stream().map(this::mapEpisodeToNode).toList());
    }

    public void migrateReviews() {
        reviewNeoRepository.saveAll(reviewRepository.findAll().stream().map(this::mapReviewToNode).toList());
    }

    public void migrateLists() {
        listsNeoRepository.saveAll(listsRepository.findAll().stream().map(this::mapListsToNode).toList());
    }

    public void migratePlans() {
        planNeoRepository.saveAll(planRepository.findAll().stream().map(this::mapPlanToNode).toList());
    }

    public void migratePaymentMethods() {
        paymentMethodNeoRepository.saveAll(paymentMethodRepository.findAll().stream().map(this::mapPaymentMethodToNode).toList());
    }

    public void migrateReceipts() {
        receiptNeoRepository.saveAll(receiptRepository.findAll().stream().map(this::mapReceiptToNode).toList());
    }

    public void migratePayments() {
        paymentNeoRepository.saveAll(paymentRepository.findAll().stream().map(this::mapPaymentToNode).toList());
    }

    public void migrateSubscriptions() {
        subscriptionNeoRepository.saveAll(subscriptionRepository.findAll().stream().map(this::mapSubscriptionToNode).toList());
    }

    public void migrateAccountUserRelationships() {
        for (Account account : accountRepository.findAll()) {
            AccountNode accountNode = accountNeoRepository.findById(account.getAccountId()).orElse(null);
            if (accountNode == null || account.getUsers() == null) continue;

            for (User user : account.getUsers()) {
                userNeoRepository.findById(user.getUsersId()).ifPresent(accountNode.getUsers()::add);
            }

            accountNeoRepository.save(accountNode);
        }
    }

    public void migrateUserProfileRelationships() {
        for (User user : userRepository.findAll()) {
            UserNode userNode = userNeoRepository.findById(user.getUsersId()).orElse(null);
            if (userNode == null || user.getAccount() == null || user.getAccount().getProfiles() == null) continue;

            for (Profile profile : user.getAccount().getProfiles()) {
                profileNeoRepository.findById(profile.getProfileId()).ifPresent(userNode.getProfiles()::add);
            }

            userNeoRepository.save(userNode);
        }
    }

    public void migrateProfileReviewRelationships() {
        for (Profile profile : profileRepository.findAll()) {
            ProfileNode profileNode = profileNeoRepository.findById(profile.getProfileId()).orElse(null);
            if (profileNode == null || profile.getReviews() == null) continue;

            for (Review review : profile.getReviews()) {
                reviewNeoRepository.findById(review.getReviewId()).ifPresent(profileNode.getReviews()::add);
            }

            profileNeoRepository.save(profileNode);
        }
    }

    public void migrateProfileListRelationships() {
        for (Profile profile : profileRepository.findAll()) {
            ProfileNode profileNode = profileNeoRepository.findById(profile.getProfileId()).orElse(null);
            if (profileNode == null || profile.getLists() == null) continue;

            for (Lists list : profile.getLists()) {
                listsNeoRepository.findById(list.getListId()).ifPresent(profileNode.getLists()::add);
            }

            profileNeoRepository.save(profileNode);
        }
    }

    public void migrateListContentRelationships() {
        for (Lists list : listsRepository.findAll()) {
            ListsNode listsNode = listsNeoRepository.findById(list.getListId()).orElse(null);
            if (listsNode == null || list.getContents() == null) continue;

            for (Content content : list.getContents()) {
                contentNeoRepository.findById(content.getContentId()).ifPresent(listsNode.getContents()::add);
            }

            listsNeoRepository.save(listsNode);
        }
    }

    public void migrateReviewContentRelationships() {
        for (Review review : reviewRepository.findAll()) {
            ReviewNode reviewNode = reviewNeoRepository.findById(review.getReviewId()).orElse(null);
            if (reviewNode == null || review.getContent() == null) continue;

            contentNeoRepository.findById(review.getContent().getContentId())
                    .ifPresent(reviewNode::setContent);

            reviewNeoRepository.save(reviewNode);
        }
    }

    public void migrateMovieGenreRelationships() {
        for (Movie movie : movieRepository.findAll()) {
            MovieNode movieNode = movieNeoRepository.findById(movie.getMovieId()).orElse(null);
            if (movieNode == null || movie.getContent() == null || movie.getContent().getGenres() == null) continue;

            for (Genre genre : movie.getContent().getGenres()) {
                genreNeoRepository.findById(genre.getGenreId()).ifPresent(movieNode.getGenres()::add);
            }

            movieNeoRepository.save(movieNode);
        }
    }

    public void migrateShowGenreRelationships() {
        for (Show show : showRepository.findAll()) {
            ShowNode showNode = showNeoRepository.findById(show.getShowsId()).orElse(null);
            if (showNode == null || show.getContent() == null || show.getContent().getGenres() == null) continue;

            for (Genre genre : show.getContent().getGenres()) {
                genreNeoRepository.findById(genre.getGenreId()).ifPresent(showNode.getGenres()::add);
            }

            showNeoRepository.save(showNode);
        }
    }

    public void migrateMoviePersonnelRelationships() {
        for (Movie movie : movieRepository.findAll()) {
            MovieNode movieNode = movieNeoRepository.findById(movie.getMovieId()).orElse(null);
            if (movieNode == null || movie.getContent() == null || movie.getContent().getPersonnel() == null) continue;

            for (Personnel person : movie.getContent().getPersonnel()) {
                personnelNeoRepository.findById(person.getPersonnelId()).ifPresent(movieNode.getPersonnel()::add);
            }

            movieNeoRepository.save(movieNode);
        }
    }

    public void migrateShowPersonnelRelationships() {
        for (Show show : showRepository.findAll()) {
            ShowNode showNode = showNeoRepository.findById(show.getShowsId()).orElse(null);
            if (showNode == null || show.getContent() == null || show.getContent().getPersonnel() == null) continue;

            for (Personnel person : show.getContent().getPersonnel()) {
                personnelNeoRepository.findById(person.getPersonnelId()).ifPresent(showNode.getPersonnel()::add);
            }

            showNeoRepository.save(showNode);
        }
    }

    public void migrateShowSeasonRelationships() {
        for (Show show : showRepository.findAll()) {
            ShowNode showNode = showNeoRepository.findById(show.getShowsId()).orElse(null);
            if (showNode == null || show.getSeasons() == null) continue;

            for (Season season : show.getSeasons()) {
                seasonNeoRepository.findById(season.getSeasonId()).ifPresent(showNode.getSeasons()::add);
            }

            showNeoRepository.save(showNode);
        }
    }

    public void migrateSeasonEpisodeRelationships() {
        for (Season season : seasonRepository.findAll()) {
            SeasonNode seasonNode = seasonNeoRepository.findById(season.getSeasonId()).orElse(null);
            if (seasonNode == null || season.getEpisodes() == null) continue;

            for (Episode episode : season.getEpisodes()) {
                episodeNeoRepository.findById(episode.getEpisodeId()).ifPresent(seasonNode.getEpisodes()::add);
            }

            seasonNeoRepository.save(seasonNode);
        }
    }

    public void migrateSubscriptionPlanRelationships() {
        for (Subscription subscription : subscriptionRepository.findAll()) {
            SubscriptionNode subscriptionNode = subscriptionNeoRepository.findById(subscription.getSubscriptionId()).orElse(null);
            if (subscriptionNode == null || subscription.getPlan() == null) continue;

            planNeoRepository.findById(subscription.getPlan().getPlanId())
                    .ifPresent(subscriptionNode::setPlan);

            subscriptionNeoRepository.save(subscriptionNode);
        }
    }

    public void migrateSubscriptionPaymentRelationships() {
        for (Subscription subscription : subscriptionRepository.findAll()) {
            SubscriptionNode subscriptionNode = subscriptionNeoRepository.findById(subscription.getSubscriptionId()).orElse(null);
            if (subscriptionNode == null || subscription.getPayments() == null || subscription.getPayments().isEmpty()) continue;

            Payment payment = subscription.getPayments().get(0);

            paymentNeoRepository.findById(payment.getPaymentId())
                    .ifPresent(subscriptionNode::setPayment);

            subscriptionNeoRepository.save(subscriptionNode);
        }
    }

    public void migratePaymentMethodRelationships() {
        for (Payment payment : paymentRepository.findAll()) {
            PaymentNode paymentNode = paymentNeoRepository.findById(payment.getPaymentId()).orElse(null);
            if (paymentNode == null || payment.getPaymentMethod() == null) continue;

            paymentMethodNeoRepository.findById(payment.getPaymentMethod().getPaymentmethodId())
                    .ifPresent(paymentNode::setPaymentmethod);

            paymentNeoRepository.save(paymentNode);
        }
    }

    public void migratePaymentReceiptRelationships() {
        for (Receipt receipt : receiptRepository.findAll()) {
            if (receipt.getPayment() == null) continue;

            PaymentNode paymentNode = paymentNeoRepository.findById(receipt.getPayment().getPaymentId()).orElse(null);
            ReceiptNode receiptNode = receiptNeoRepository.findById(receipt.getReceiptId()).orElse(null);

            if (paymentNode == null || receiptNode == null) continue;

            paymentNode.setReceipt(receiptNode);
            paymentNeoRepository.save(paymentNode);
        }
    }

    public void migrateAccountSubscriptionRelationships() {
        for (Account account : accountRepository.findAll()) {
            AccountNode accountNode = accountNeoRepository.findById(account.getAccountId()).orElse(null);
            if (accountNode == null || account.getSubscriptions() == null) continue;

            for (Subscription subscription : account.getSubscriptions()) {
                subscriptionNeoRepository.findById(subscription.getSubscriptionId())
                        .ifPresent(accountNode.getSubscriptions()::add);
            }

            accountNeoRepository.save(accountNode);
        }
    }

    private AccountNode mapAccountToNode(Account account) {
        AccountNode node = new AccountNode();
        node.setId(account.getAccountId());
        node.setEmail(account.getMail());
        return node;
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

    private ContentNode mapContentToNode(Content content) {
        ContentNode node = new ContentNode();
        node.setId(content.getContentId());
        node.setOriginaltitle(content.getOriginaltitle());
        node.setTitle(content.getTitle());
        node.setDescription(content.getDescription());
        node.setRating(content.getRating());
        node.setReleasedate(content.getReleasedate());
        node.setThumbnail(content.getThumbnail());
        node.setType(content.getType());
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

    private SeasonNode mapSeasonToNode(Season season) {
        SeasonNode node = new SeasonNode();
        node.setId(season.getSeasonId());
        return node;
    }

    private EpisodeNode mapEpisodeToNode(Episode episode) {
        EpisodeNode node = new EpisodeNode();
        node.setId(episode.getEpisodeId());
        node.setTitle(episode.getTitle());

        if (episode.getDuration() != null) {
            node.setDurationminutes(episode.getDuration().intValue());
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

    private ListsNode mapListsToNode(Lists list) {
        ListsNode node = new ListsNode();
        node.setId(list.getListId());
        node.setName(list.getListname());
        return node;
    }

    private PlanNode mapPlanToNode(Plan plan) {
        PlanNode node = new PlanNode();
        node.setId(plan.getPlanId());
        node.setName(plan.getName());
        node.setPrice(plan.getPrice());
        node.setDescription(plan.getDescription());
        return node;
    }

    private PaymentMethodNode mapPaymentMethodToNode(PaymentMethod method) {
        PaymentMethodNode node = new PaymentMethodNode();
        node.setId(method.getPaymentmethodId());

        if (method.getType() != null) {
            node.setMethodname(method.getType().name());
        }

        return node;
    }

    private ReceiptNode mapReceiptToNode(Receipt receipt) {
        ReceiptNode node = new ReceiptNode();
        node.setId(receipt.getReceiptId());
        node.setReceiptnumber(receipt.getReceiptNumber());
        node.setCreatedat(receipt.getPaydate());
        return node;
    }

    private PaymentNode mapPaymentToNode(Payment payment) {
        PaymentNode node = new PaymentNode();
        node.setId(payment.getPaymentId());
        node.setAmount(payment.getPrice());
        node.setPaymentdate(payment.getCreatedAt());

        if (payment.getStatus() != null) {
            node.setStatus(payment.getStatus().name());
        }

        return node;
    }

    private SubscriptionNode mapSubscriptionToNode(Subscription subscription) {
        SubscriptionNode node = new SubscriptionNode();
        node.setId(subscription.getSubscriptionId());
        node.setStartdate(subscription.getStartdate());
        node.setEnddate(subscription.getEnddate());

        if (subscription.getStatus() != null) {
            node.setStatus(subscription.getStatus().name());
        }

        return node;
    }
}