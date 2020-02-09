package com.opendataview.web.pages.contact;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import com.opendataview.web.pages.index.BasePage;

public class ContactPage extends BasePage {

	private static final long serialVersionUID = 1L;

	// private final static Logger log = LoggerFactory.getLogger(ContactPage.class);

	public ContactPage() {

		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setVisible(false);
		add(feedback);

		final Form<?> sendEmailForm = new Form<Void>("sendEmailForm");
		add(sendEmailForm);

		final TextField<String> nameInput = new TextField<String>("name", Model.of(""));
		final EmailTextField emailInput = new EmailTextField("email", Model.of(""));
		final TextArea<String> messageInput = new TextArea<String>("message", Model.of(""));
		sendEmailForm.add(nameInput);
		sendEmailForm.add(emailInput);
		sendEmailForm.add(messageInput);

		Button submitButtonSendEmail = new Button("submitButtonSendEmail") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				super.onSubmit();
				String name = nameInput.getModelObject();
				String email = emailInput.getModelObject();
				String message = messageInput.getModelObject();

				try {
					MailClient client = new MailClient();
					String server = "smtp.gmail.com";
					String from = "opendatav@gmail.com";
					String to = "david@dpastrana.com";
					String subject = "From: name: " + name + ", email:" + email;
					String message2 = "Message:\n\n" + message;
					// String[] filenames = { "test.txt", "test2.txt" };
					client.sendMail(server, from, to, subject, message2, null);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					error("We are sorry, an error occurred being " + e.getLocalizedMessage());
					feedback.setVisible(true);
				}
				success("Thank you for contacting us! We will respond to you shortly.");
				feedback.setVisible(true);
			}
		};
		sendEmailForm.add(submitButtonSendEmail);
	}
}
