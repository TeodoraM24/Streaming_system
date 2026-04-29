package org.example.neo4j.repositories;

import org.example.neo4j.nodes.ProfileNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ProfileNeoRepository extends Neo4jRepository<ProfileNode, Long> {
}