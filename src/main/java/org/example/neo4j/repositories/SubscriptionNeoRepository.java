package org.example.neo4j.repositories;

import org.example.neo4j.nodes.SubscriptionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SubscriptionNeoRepository extends Neo4jRepository<SubscriptionNode, Long> {
}