package org.example.mongo.repositories;

import org.example.mongo.documents.ListsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ListsMongoRepository extends MongoRepository<ListsDocument, String> {
}
