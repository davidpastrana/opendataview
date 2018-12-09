package com.opendataview.web.pages.index;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

public class AppWebSession extends AuthenticatedWebSession {

  private static final long serialVersionUID = 1L;


  public AppWebSession(Request request) {
    super(request);
  }

  /**
   * @see org.apache.wicket.authentication.AuthenticatedWebSession#authenticate(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean authenticate(final String username, final String password) {
    final String WICKET = "wicket";

    // Check username and password
    return WICKET.equals(username) && WICKET.equals(password);
  }

  /**
   * @see org.apache.wicket.authentication.AuthenticatedWebSession#getRoles()
   */
  @Override
  public Roles getRoles() {
    if (isSignedIn()) {
      // If the user is signed in, they have these roles
      return new Roles(Roles.ADMIN);
    }
    return null;
  }
}
