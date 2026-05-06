package org.example.repositories;
import org.example.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByMail(String mail);
    Optional<Account> findByPhonenumber(String phonenumber);
}