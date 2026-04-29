package org.example.mongo.repositories;

import org.example.mongo.documents.PaymentMethodDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentMethodMongoRepository extends MongoRepository<PaymentMethodDocument, String> {
}
