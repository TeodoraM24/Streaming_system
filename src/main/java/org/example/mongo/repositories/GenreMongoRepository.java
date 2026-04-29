package org.example.mongo.repositories;

import org.example.mongo.documents.GenreDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenreMongoRepository extends MongoRepository<GenreDocument, String> {
}
