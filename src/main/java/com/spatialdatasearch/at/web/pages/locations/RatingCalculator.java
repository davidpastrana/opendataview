package com.spatialdatasearch.at.web.pages.locations;

// helper class,

public class RatingCalculator {
  public double calculateRating(int newRating, int nRatings, double oldRating) {
    double calculated_rating = oldRating;
    calculated_rating += newRating;
    calculated_rating /= nRatings;
    return calculated_rating;
  }
}
