package com.spatialdatasearch.at.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity(name = "locations")
@Table(name = "locations")
public class LocationModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Generated(GenerationTime.INSERT)
  @Column(name = "id", unique = true)
  protected Long id;
  @Column(columnDefinition = "TEXT")
  protected String name;
  @Column(columnDefinition = "TEXT")
  protected String type;
  @Column(columnDefinition = "TEXT")
  protected String address;
  @Column(columnDefinition = "TEXT")
  protected String district;
  @Column(columnDefinition = "TEXT")
  protected String city;
  @Column(columnDefinition = "TEXT")
  protected String postcode;
  @Column(columnDefinition = "TEXT")
  protected String country;
  @Column(columnDefinition = "TEXT")
  protected String elevation;
  @Column(columnDefinition = "TEXT")
  protected String population;
  @Column(columnDefinition = "decimal", precision = 2, scale = 15)
  protected BigDecimal latitude;
  @Column(columnDefinition = "decimal", precision = 2, scale = 15)
  protected BigDecimal longitude;
  @Column(columnDefinition = "TEXT")
  private String website;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(columnDefinition = "TEXT")
  private String email;
  @Column(columnDefinition = "TEXT")
  private String phone;
  @Column(columnDefinition = "TEXT")
  protected String date;
  @Column(columnDefinition = "TEXT")
  protected String schedule;
  @Column(columnDefinition = "TEXT")
  protected String urlImage;
  @Column(columnDefinition = "TEXT")
  protected String csvName;
  @Column(columnDefinition = "TEXT")
  protected String other;
  protected double rating = 0;
  private int nrating = 0;


  @Column(columnDefinition = "TEXT")
  protected String street;
  @Column(columnDefinition = "TEXT")
  protected String number;

  public LocationModel() {}

  public String getAddress() {
    return address;
  }

  public String getCsvName() {
    return csvName;
  }

  public String getDate() {
    return date;
  }

  public String getDescription() {
    return description;
  }

  public String getDistrict() {
    return district;
  }

  public String getEmail() {
    return email;
  }

  public Long getId() {
    return id;
  }

  public String getImage() {
    return urlImage;
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

  public String getNumber() {
    return number;
  }

  public String getPhone() {
    return phone;
  }

  public double getRating() {
    return rating;
  }

  public String getSchedule() {
    return schedule;
  }

  public String getStreet() {
    return street;
  }

  public String getType() {
    return type;
  }

  public String getWebsite() {
    return website;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setCsvName(String csvName) {
    this.csvName = csvName;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setImage(String urlImage) {
    this.urlImage = urlImage;
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

  public void setNumber(String number) {
    this.number = number;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getElevation() {
    return elevation;
  }

  public void setElevation(String elevation) {
    this.elevation = elevation;
  }

  public String getPopulation() {
    return population;
  }

  public void setPopulation(String population) {
    this.population = population;
  }

  public String getUrlImage() {
    return urlImage;
  }

  public void setUrlImage(String urlImage) {
    this.urlImage = urlImage;
  }

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }
}
