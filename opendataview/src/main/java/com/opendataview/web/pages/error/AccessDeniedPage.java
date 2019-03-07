package com.opendataview.web.pages.error;

import java.io.IOException;

import com.opendataview.web.pages.index.BasePage;

public class AccessDeniedPage extends BasePage {

	protected AccessDeniedPage() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected String getMessage() {
		return "Access Denied: You do not have access to the requested page.";
	}
}