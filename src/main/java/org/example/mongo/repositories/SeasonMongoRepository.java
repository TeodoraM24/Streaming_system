package org.example.mongo.repositories;

import org.example.mongo.documents.SeasonDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SeasonMongoRepository extends MongoRepository<SeasonDocument, String> {
}
