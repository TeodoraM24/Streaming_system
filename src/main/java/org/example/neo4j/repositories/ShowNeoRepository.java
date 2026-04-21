package org.example.neo4j.repositories;

import org.example.neo4j.nodes.ShowNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ShowNeoRepository extends Neo4jRepository<ShowNode, Long> {
}