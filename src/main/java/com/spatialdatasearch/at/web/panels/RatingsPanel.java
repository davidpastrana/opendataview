package com.spatialdatasearch.at.web.panels;

import org.apache.wicket.extensions.rating.RatingPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public abstract class RatingsPanel extends RatingPanel {

  private static final long serialVersionUID = 1L;

  @Override
  protected String getActiveStarUrl(int iteration) {

    return "/images/rating/rated-star-icon.png";
  }

  @Override
  protected String getInactiveStarUrl(int iteration) {

    return "/images/rating/star-icon.png";
  }

  /**
   * Star image for no selected star
   */
  public static final ResourceReference STAR0 = new PackageResourceReference(RatingPanel.class,
      "star0.gif");

  /**
   * Star image for selected star
   */
  public static final ResourceReference STAR1 = new PackageResourceReference(RatingPanel.class,
      "star1.gif");

  public RatingsPanel(String id, IModel model, int numberOfStars, boolean defaultCss) {
    super(id, model, numberOfStars, defaultCss);
  }

}
