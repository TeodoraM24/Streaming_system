package org.example.mongo.repositories;

import org.example.mongo.documents.PaymentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentMongoRepository extends MongoRepository<PaymentDocument, String> {
}
