package com.beple.ex_2fa.domain.user;

import com.beple.ex_2fa.domain.token.Token;
import com.beple.ex_2fa.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;
  private String firstname;
  private String lastname;
  private String username;
  private String password;
  private String profile;
  @Builder.Default
  private boolean status=true;

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany
  private List<Token> tokens;

  private String scrKey;

  @Builder.Default
  private boolean enable2fa=false;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return status;
  }
}
