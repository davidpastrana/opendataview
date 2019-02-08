package com.opendataview.web.pages.user;

import com.opendataview.web.pages.index.BasePage;

public class LogoutUserPage extends BasePage {

  private static final long serialVersionUID = 1L;

  public LogoutUserPage() {

    getSession().invalidate();
    setResponsePage(LoginUserPage.class);
  }
}
