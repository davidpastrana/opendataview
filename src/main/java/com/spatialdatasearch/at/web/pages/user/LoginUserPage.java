package com.spatialdatasearch.at.web.pages.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spatialdatasearch.at.model.UserModel;
import com.spatialdatasearch.at.persistence.UserServiceDAO;
import com.spatialdatasearch.at.web.pages.index.BasePage;
import com.spatialdatasearch.at.web.pages.locations.LocationServicePage;
import com.spatialdatasearch.at.web.pages.upload.UploadFilePage;

public class LoginUserPage extends BasePage {

  private static final long serialVersionUID = 1L;

  private final static Logger log = LoggerFactory.getLogger(LoginUserPage.class);

  @SpringBean
  private UserServiceDAO userServiceDAO;

  protected List<UserModel> listUsers = new ArrayList<UserModel>();

  public LoginUserPage() {

    final TextField<String> username = new TextField<String>("username", Model.of(""));
    final PasswordTextField password = new PasswordTextField("password", Model.of(""));
    add(new FeedbackPanel("feedback"));

    final Form<?> userForm = new Form<Void>("userForm") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {
        super.onSubmit();

        final String usr = username.getModelObject();
        final String pwd = password.getModelObject();

        listUsers = userServiceDAO.readUserModel(usr, pwd);

        boolean find = false;
        for (UserModel u : listUsers) {
          if (u.getUsername().contentEquals(usr) && u.getPassword().contentEquals(pwd)) {

            if (usr.contentEquals("admin")) {
              setResponsePage(UploadFilePage.class);
            } else {
              setResponsePage(LocationServicePage.class);
            }

            WebSession session = WebSession.get();
            session.setAttribute("user_name", usr);
            log.info("Session is.... " + session.getId());
            log.info("Size is.... " + session.getSizeInBytes() + " bytes");
            log.info("Session name.... " + session.getAttribute("user_name"));
            find = true;
            break;
          }
        }
        if (!find)
          info("The username/password introduced is not correct.");

      }

    };
    add(userForm);
    userForm.add(username);
    userForm.add(password);

    WebSession session = WebSession.get();

    if (session.getAttribute("user_name") != null) {
      userForm.setVisible(false);
    } else {
      userForm.setVisible(true);
    }

  }

}
