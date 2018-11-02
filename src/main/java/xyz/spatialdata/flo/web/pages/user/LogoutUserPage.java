package xyz.spatialdata.flo.web.pages.user;

import xyz.spatialdata.flo.web.pages.index.BasePage;

public class LogoutUserPage extends BasePage {

  private static final long serialVersionUID = 1L;

  public LogoutUserPage() {

    getSession().invalidate();
    setResponsePage(LoginUserPage.class);
  }
}
