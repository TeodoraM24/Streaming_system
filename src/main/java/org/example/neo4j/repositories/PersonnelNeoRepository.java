package org.example.neo4j.repositories;

import org.example.neo4j.nodes.PersonnelNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PersonnelNeoRepository extends Neo4jRepository<PersonnelNode, Long> {
}