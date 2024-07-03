package com.example.oauth2.token;


import com.example.oauth2.model.User;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter

@Entity
public class Token {

  @Id
  @GeneratedValue
  public Integer id;

  @Column(unique = true)
  public String token;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  public boolean revoked;

  public boolean expired;
  private LocalDateTime expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id")
  public User user;


}
