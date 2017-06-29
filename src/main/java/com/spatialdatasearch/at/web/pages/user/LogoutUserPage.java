package com.spatialdatasearch.at.web.pages.user;

import com.spatialdatasearch.at.web.pages.index.BasePage;

public class LogoutUserPage extends BasePage {

  private static final long serialVersionUID = 1L;

  public LogoutUserPage() {

    getSession().invalidate();
    setResponsePage(LoginUserPage.class);
  }
}
