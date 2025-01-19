package com.friney.fairsplit.core.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@Table(name = "not_registered_users")
@AllArgsConstructor
public class NotRegisteredUser extends User {
}
