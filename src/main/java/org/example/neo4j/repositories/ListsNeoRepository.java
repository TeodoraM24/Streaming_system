package org.example.neo4j.repositories;

import org.example.neo4j.nodes.ListsNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ListsNeoRepository extends Neo4jRepository<ListsNode, Long> {
}