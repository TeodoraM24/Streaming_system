package org.example.mongo.repositories;

import org.example.mongo.documents.ContentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentMongoRepository extends MongoRepository<ContentDocument, String> {
}
