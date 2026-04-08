package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ProfileDTO;
import java.util.List;

@Entity
@Table(name = "profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    private String profilename;

    @ManyToOne
    @JoinColumn(name = "accounts_account_id")
    @JsonIgnore // Prevents loop back to Account
    private Account account;

    @OneToMany(mappedBy = "profile")
    private List<Lists> lists;

    @OneToMany(mappedBy = "profile")
    private List<Review> reviews;

    public Profile(ProfileDTO dto) {
        this.profileId = dto.getProfileId();
        this.profilename = dto.getProfilename();
    }
}