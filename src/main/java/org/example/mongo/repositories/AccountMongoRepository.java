package org.example.mongo.repositories;

import org.example.mongo.documents.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountMongoRepository extends MongoRepository<AccountDocument, String> {
}
