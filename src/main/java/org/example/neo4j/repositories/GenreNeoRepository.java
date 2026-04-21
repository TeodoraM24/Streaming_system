package org.example.neo4j.repositories;

import org.example.neo4j.nodes.GenreNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GenreNeoRepository extends Neo4jRepository<GenreNode, Long> {
}