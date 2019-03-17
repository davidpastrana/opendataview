package com.opendataview.web.pages.error;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendataview.web.pages.index.BasePage;

public class ErrorPage404 extends BasePage {

	private final static Logger log = LoggerFactory.getLogger(ErrorPage404.class);

	private static final long serialVersionUID = 1L;

	private final IModel<String> errorModel = new Model<String>();
	Label errorCode = new Label("errorCode", errorModel);

//	public ErrorPage404() {
//		// errorModel.setObject("The page does not exist!!!!");
//		errorModel.setObject(e.getMessage());
//	}

	public ErrorPage404(final Exception e) {
		StringWriter errors = new StringWriter();
//		errors.append("Sorry we have receive the followin error:\n" + e);

		final WebSession session = WebSession.get();

		e.printStackTrace(new PrintWriter(errors));
//		if (session.getAttribute("user_name") == null) {
//			errors.flush();
//			errors.append("No user available. Page seems to be expired.");
//			throw new PageExpiredException("No user available. Page seems to be expired.");
//		} else {
//			errors.append("Session is ok for: " + session.getAttribute("user_name").toString());
//		}
		log.info("error is: " + errors);
		errorModel.setObject(errors.toString());

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
		errorCode.setOutputMarkupId(false);
		errorCode.setOutputMarkupPlaceholderTag(true);
		add(errorCode);
	}
}
