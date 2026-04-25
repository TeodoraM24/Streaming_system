package org.example.mongo.repositories;

import org.example.mongo.documents.MovieDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieMongoRepository extends MongoRepository<MovieDocument, String> {
}
