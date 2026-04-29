package org.example.mongo.repositories;

import org.example.mongo.documents.ProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileMongoRepository extends MongoRepository<ProfileDocument, String> {
}
