package org.example.repositories;
import org.example.entities.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {}