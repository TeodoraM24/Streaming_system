package org.example.mongo.repositories;

import org.example.mongo.documents.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
}
