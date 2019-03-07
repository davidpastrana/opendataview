package com.opendataview.web.pages.error;

import java.io.IOException;

public class InternalErrorPage extends BaseMessagePage {

	public InternalErrorPage() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Internal error occurred";
	}
}
