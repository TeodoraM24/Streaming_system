package org.example.neo4j.repositories;

import org.example.neo4j.nodes.PaymentNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PaymentNeoRepository extends Neo4jRepository<PaymentNode, Long> {
}