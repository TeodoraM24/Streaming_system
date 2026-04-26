package org.example.neo4j.repositories;

import org.example.neo4j.nodes.SeasonNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SeasonNeoRepository extends Neo4jRepository<SeasonNode, Long> {
}