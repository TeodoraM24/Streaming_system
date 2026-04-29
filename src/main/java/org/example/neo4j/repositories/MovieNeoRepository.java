package org.example.neo4j.repositories;

import org.example.neo4j.nodes.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MovieNeoRepository extends Neo4jRepository<MovieNode, Long> {
}