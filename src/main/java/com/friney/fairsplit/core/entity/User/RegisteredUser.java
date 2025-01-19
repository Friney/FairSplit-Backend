package com.friney.fairsplit.core.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuperBuilder
@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@Table(name = "registered_users")
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredUser extends User {
    private String email;
}
