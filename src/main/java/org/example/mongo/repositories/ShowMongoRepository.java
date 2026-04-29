package org.example.mongo.repositories;

import org.example.mongo.documents.ShowDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowMongoRepository extends MongoRepository<ShowDocument, String> {
}
