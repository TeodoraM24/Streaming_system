package org.example.mongo.repositories;

import org.example.mongo.documents.PersonnelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonnelMongoRepository extends MongoRepository<PersonnelDocument, String> {
}
