package org.example.neo4j.repositories;

import org.example.neo4j.nodes.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserNeoRepository extends Neo4jRepository<UserNode, Long> {
}