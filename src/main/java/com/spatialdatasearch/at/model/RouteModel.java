package com.spatialdatasearch.at.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name = "routes")
@Table(name = "routes")
public class RouteModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "routes_sequence", sequenceName = "routes_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routes_sequence")
  @Column(name = "id", updatable = false, nullable = false)
  protected Long id_route;
  @Column(name = "id_marker", nullable = false)
  protected Long id;
  @Column(columnDefinition = "TEXT")
  protected String name;
  @Column(name = "latitude", columnDefinition = "decimal", precision = 2, scale = 5)
  protected BigDecimal latitude;
  @Column(name = "longitude", columnDefinition = "decimal", precision = 2, scale = 5)
  protected BigDecimal longitude;
  @Column(columnDefinition = "TEXT")
  protected String price;
  @Column(columnDefinition = "TEXT")
  protected String days;
  protected double rating = 0;
  private int nrating = 0;

  public RouteModel() {

  }

  public String getDays() {
    return days;
  }

  public Long getId() {
    return id;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public String getName() {
    return name;
  }

  public int getNrating() {
    return nrating;
  }

  public String getPrice() {
    return price;
  }

  public double getRating() {
    return rating;
  }

  public void setDays(String days) {
    this.days = days;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNrating(int nrating) {
    this.nrating = nrating;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }
}
