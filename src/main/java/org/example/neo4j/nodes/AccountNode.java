package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountNode {

    @Id
    private Long id;

    private String email;
    private String password;

    @Relationship(type = "HAS_USER")
    private UserNode user;

    @Relationship(type = "HAS_SUBSCRIPTION")
    private Set<SubscriptionNode> subscriptions = new HashSet<>();
}