package org.example.mongo.repositories;

import org.example.mongo.documents.ReviewDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewMongoRepository extends MongoRepository<ReviewDocument, String> {
}
