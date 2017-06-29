package com.spatialdatasearch.at.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name = "users")
@Table(name = "users")
public class UserModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "users_sequence", sequenceName = "users_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence")
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id;
  @Column(columnDefinition = "TEXT", nullable = false, length = 10)
  protected String username;
  @Column(columnDefinition = "TEXT", nullable = false, length = 20)
  protected String email;
  @Column(columnDefinition = "TEXT", nullable = false, length = 10)
  protected String password;

  public UserModel() {

  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

}
