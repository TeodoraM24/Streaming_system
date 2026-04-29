package org.example.neo4j.repositories;

import org.example.neo4j.nodes.ReceiptNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ReceiptNeoRepository extends Neo4jRepository<ReceiptNode, Long> {
}