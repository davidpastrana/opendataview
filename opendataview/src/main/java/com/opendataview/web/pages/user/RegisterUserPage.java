package com.opendataview.web.pages.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.opendataview.web.model.UserModel;
import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.persistence.PropertiesServiceDAO;
import com.opendataview.web.persistence.UserServiceDAO;

public class RegisterUserPage extends BasePage {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private UserServiceDAO userServiceDAO;

	@SpringBean
	private PropertiesServiceDAO propertiesServiceDAO;

	protected boolean user_exists = false;
	protected boolean email_exists = false;

	public RegisterUserPage() {

		FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setVisible(false);
		add(feedback);

		String password2 = "";
		final TextField<String> username = new TextField<String>("username", Model.of(""));
		final EmailTextField email = new EmailTextField("email", Model.of(""));

		final PasswordTextField password = new PasswordTextField("password", Model.of(""));
		password.setLabel(Model.of("Password"));

		final PasswordTextField cpassword = new PasswordTextField("cpassword", Model.of(""));
		cpassword.setLabel(Model.of("Confirm Password"));

		final Label result = new Label("result", " " + password2);
		add(result);

		final Form<?> userForm = new Form<Void>("userForm") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				super.onSubmit();

				final String usr = username.getModelObject();
				final String em = email.getModelObject();
				final String pwd = password.getModelObject();

				user_exists = userServiceDAO.isUsernameRegistered(usr);
				email_exists = userServiceDAO.isEmailRegistered(em);

				// Pattern: 1 digit, 1 lower, from 6 to 20
				Pattern p = Pattern.compile("((?=.*\\d)(?=.*[a-z]).{6,20})");
				Matcher m = p.matcher(pwd);
				if (!m.find()) {
					error("The password must contain a minimum of 6 characters, including at least one number and one letter");
					feedback.setVisible(true);
				} else if (user_exists) {
					error("User \"" + usr + "\" already exists! Please select a different User ID");
					feedback.setVisible(true);
				} else if (email_exists) {
					error("Email \"" + em + "\" already exists! Please select a different Email address");
					feedback.setVisible(true);
				} else {
					UserModel user = new UserModel();
					user.setUsername(usr);
					user.setEmail(em);
					user.setPassword(pwd);
					propertiesServiceDAO.createPropertiesModel(usr);
					userServiceDAO.registerUserModel(user);
					this.setVisible(false);
					success("User \"" + usr + "\" has been successfully created!");
//            feedback.add(new AttributeModifier("class", String.valueOf(this.getMarkupAttributes().get("class")).replaceFirst("error", ""))); 
//            feedback.add(AttributeModifier.append("class", "success"));
					feedback.setVisible(true);
				}

			}

		};
		add(userForm);
		userForm.add(username);
		userForm.add(email);
		userForm.add(password);
		userForm.add(cpassword);
		userForm.add(new EqualPasswordInputValidator(password, cpassword));
	}
}
