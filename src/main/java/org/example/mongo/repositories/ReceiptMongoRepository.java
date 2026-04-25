package org.example.mongo.repositories;

import org.example.mongo.documents.ReceiptDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReceiptMongoRepository extends MongoRepository<ReceiptDocument, String> {
}
