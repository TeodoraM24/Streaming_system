package org.example.neo4j.repositories;

import org.example.neo4j.nodes.PaymentMethodNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PaymentMethodNeoRepository extends Neo4jRepository<PaymentMethodNode, Long> {
}