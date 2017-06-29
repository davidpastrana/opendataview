package com.spatialdatasearch.at.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;

@Entity(name = "ratings")
@Table(name = "ratings")
public class RatingModel implements IClusterable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "ratings_sequence", sequenceName = "ratings_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ratings_sequence")
  @Column(name = "id", updatable = false, nullable = false)
  private Long location_id;

  @ManyToOne
  private LocationModel location;

  private int rating = 0;

  public RatingModel() {

  }

  public RatingModel(int rating) {
    this.rating = rating;
  }

  public Long getId() {
    return location.getId();
  }

  public LocationModel getLocation() {
    return location;
  }

  public int getNumberOfVotes() {
    return 1;
  }

  public int getRating() {
    return rating;
  }

  public boolean isActive(int star) {
    return star < rating;
  }

  public void setLocation(LocationModel location) {
    this.location = location;
  }

  public void updateRating(int numberOfStars) {
    rating = numberOfStars;
  }
}
