package org.example.repositories;
import org.example.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {}