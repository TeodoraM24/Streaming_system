package org.example.mongo.repositories;

import org.example.mongo.documents.EpisodeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EpisodeMongoRepository extends MongoRepository<EpisodeDocument, String> {
}
