package org.example.neo4j.repositories;

import org.example.neo4j.nodes.ReviewNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ReviewNeoRepository extends Neo4jRepository<ReviewNode, Long> {
}