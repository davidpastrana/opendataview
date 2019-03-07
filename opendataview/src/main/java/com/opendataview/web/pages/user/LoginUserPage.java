package com.opendataview.web.pages.user;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendataview.web.model.UserModel;
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.pages.properties.SetPropertiesPage;
import com.opendataview.web.persistence.UserServiceDAO;

public class LoginUserPage extends BasePage {

	private static final long serialVersionUID = 1L;

	private final static Logger log = LoggerFactory.getLogger(LoginUserPage.class);

	@SpringBean
	private UserServiceDAO userServiceDAO;

	protected UserModel user = new UserModel();

	public LoginUserPage() {

		final TextField<String> username = new TextField<String>("username", Model.of(""));
		final PasswordTextField password = new PasswordTextField("password", Model.of(""));

		FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setVisible(false);
		add(feedback);

		final Form<?> userForm = new Form<Void>("userForm") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				super.onSubmit();

				final String usr = username.getModelObject();
				final String pwd = password.getModelObject();

				user = userServiceDAO.getUser(usr, pwd);

				if (user != null && user.getUsername().contentEquals(usr) && user.getPassword().contentEquals(pwd)) {

//        	  info("Login Success!");
//              feedback.add(AttributeModifier.append("class", "success"));
//              feedback.setVisible(true);

//            if (usr.contentEquals("admin")) {
//              setResponsePage(SetPropertiesPage.class);
//            } else {
//              setResponsePage(LocationServicePage.class);
//            }

					WebSession session = WebSession.get();
					session.setAttribute("user_name", usr);
					log.info("Session is.... " + session.getId());
					log.info("Size is.... " + session.getSizeInBytes() + " bytes");
					log.info("Session name.... " + session.getAttribute("user_name"));
					PageParameters parameters = new PageParameters();
					parameters.set("usr", usr);
					setResponsePage(SetPropertiesPage.class, parameters);
				} else {
					info("Username or password not correct. Please try again.");
					feedback.setVisible(true);
				}

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
