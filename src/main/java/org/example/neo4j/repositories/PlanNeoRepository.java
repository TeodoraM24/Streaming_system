package org.example.neo4j.repositories;

import org.example.neo4j.nodes.PlanNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PlanNeoRepository extends Neo4jRepository<PlanNode, Long> {
}