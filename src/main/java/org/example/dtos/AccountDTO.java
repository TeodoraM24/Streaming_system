package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Account;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private Long accountId;
    private String firstname;
    private String lastname;
    private String phonenumber;
    private String mail;

    public static AccountDTO convertToDTO(Account account) {
        if (account == null) {
            return null;
        }

        return new AccountDTO(
                account.getAccountId(),
                account.getFirstname(),
                account.getLastname(),
                account.getPhonenumber(),
                account.getMail()
        );
    }
}