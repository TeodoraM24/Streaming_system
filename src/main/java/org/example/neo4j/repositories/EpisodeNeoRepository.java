package org.example.neo4j.repositories;

import org.example.neo4j.nodes.EpisodeNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EpisodeNeoRepository extends Neo4jRepository<EpisodeNode, Long> {
}