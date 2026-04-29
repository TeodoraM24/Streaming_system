package org.example.mongo.repositories;

import org.example.mongo.documents.SubscriptionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionMongoRepository extends MongoRepository<SubscriptionDocument, String> {
}
