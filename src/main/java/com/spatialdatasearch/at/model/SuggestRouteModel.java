package com.spatialdatasearch.at.model;

import java.io.Serializable;


public class SuggestRouteModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private double rating = 0;
  private int nrating = 0;
  private double score = 0;

  public SuggestRouteModel() {

  }

  public String getName() {
    return name;
  }

  public int getNrating() {
    return nrating;
  }

  public double getRating() {
    return rating;
  }

  public double getScore() {
    return score;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNrating(int nrating) {
    this.nrating = nrating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public void setScore(double score) {
    this.score = score;
  }

}
