package com.spatialdatasearch.at.web.pages.user;

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

import com.spatialdatasearch.at.model.UserModel;
import com.spatialdatasearch.at.persistence.UserServiceDAO;
import com.spatialdatasearch.at.web.pages.index.BasePage;


public class RegisterUserPage extends BasePage {

  private static final long serialVersionUID = 1L;

  @SpringBean
  private UserServiceDAO userServiceDAO;

  public RegisterUserPage() {
      
    add(new FeedbackPanel("feedback"));
    
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

        UserModel user = new UserModel();

        final String usr = username.getModelObject();
        final String em = email.getModelObject();
        final String pwd = password.getModelObject();
        
        //1 digit, 1 lower, 1 upper, 1 symbol "@#$%", from 6 to 20
        //"((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
        
        //Pattern: 1 digit, 1 lower, from 6 to 20
        Pattern p = Pattern.compile("((?=.*\\d)(?=.*[a-z]).{6,20})");
        Matcher m = p.matcher(pwd);
        if (!m.find()) {
            info("Password should contains at least 1 digit, 1 lower, lenght from 6 to 20 ");
        } else {
            user.setUsername(usr);
            user.setEmail(em);
            user.setPassword(pwd);

            userServiceDAO.registerUserModel(user);
            this.setVisible(false);
            info("Thanks, your username \"" + user.getUsername() + "\" was correcty registered.");
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
