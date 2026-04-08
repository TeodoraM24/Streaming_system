package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.AccountDTO;

import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id") // Matches BIGSERIAL account_id in SQL
    private Long accountId;

    private String firstname;
    private String lastname;
    private String phonenumber;

    @Column(unique = true, nullable = false)
    private String mail;

    @OneToMany(mappedBy = "account")
    @JsonIgnore // Stops: Account -> User -> Account loop
    private List<User> users;

    @OneToMany(mappedBy = "account")
    @JsonIgnore // Stops: Account -> Profile -> Account loop
    private List<Profile> profiles;

    @OneToMany(mappedBy = "account")
    @JsonIgnore // Stops: Account -> Subscription -> Account loop
    private List<Subscription> subscriptions;

    // Constructor for creating/updating from DTO
    public Account(AccountDTO dto) {
        if (dto != null) {
            this.accountId = dto.getAccountId();
            this.firstname = dto.getFirstname();
            this.lastname = dto.getLastname();
            this.phonenumber = dto.getPhonenumber();
            this.mail = dto.getMail();
        }
    }
}