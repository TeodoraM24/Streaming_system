package org.example.mongo.repositories;

import org.example.mongo.documents.PlanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanMongoRepository extends MongoRepository<PlanDocument, String> {
}
