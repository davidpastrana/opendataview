package com.spatialdatasearch.at.web.pages.error;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.spatialdatasearch.at.web.pages.index.BasePage;

public class ErrorPage404 extends BasePage {

  private static final long serialVersionUID = 1L;

  private final IModel<String> errorModel = new Model<String>();

  public ErrorPage404() {
    errorModel.setObject("The page does not exist.");
  }

  public ErrorPage404(final Exception e) {
    errorModel.setObject(e.getMessage());
  }

  @Override
  public boolean isErrorPage() {
    return true;
  }

  @Override
  public boolean isVersioned() {
    return false;
  }

  @Override
  protected void onInitialize() {
    super.onInitialize();
    add(new Label("errorCode", errorModel));
  }
}
