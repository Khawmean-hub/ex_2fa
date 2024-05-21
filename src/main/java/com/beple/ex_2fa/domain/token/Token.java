package com.beple.ex_2fa.domain.token;

import com.beple.ex_2fa.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Token {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  public Long id;

  @Column(unique = true)
  public String token;

  public boolean revoked;

  public boolean expired;

  @ManyToOne(fetch = FetchType.LAZY)
  public User user;
}
