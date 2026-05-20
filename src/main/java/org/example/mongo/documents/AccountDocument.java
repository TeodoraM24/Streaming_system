package org.example.mongo.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.example.mongo.embedded.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "accounts")
public class AccountDocument {
    @Id
    private String id;

    private Long accountId;
    private String firstname;
    private String lastname;
    private String phonenumber;
    private String mail;

    private List<UserEmbedded> users;
    private List<ProfileEmbedded> profiles;
    private List<SubscriptionEmbedded> subscriptions;
    private List<PaymentMethodEmbedded> paymentMethods;
}