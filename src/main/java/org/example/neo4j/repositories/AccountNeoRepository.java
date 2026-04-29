package org.example.neo4j.repositories;

import org.example.neo4j.nodes.AccountNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AccountNeoRepository extends Neo4jRepository<AccountNode, Long> {
}