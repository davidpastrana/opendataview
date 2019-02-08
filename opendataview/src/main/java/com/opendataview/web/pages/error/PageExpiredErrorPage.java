package com.opendataview.web.pages.error;

import java.io.IOException;

public class PageExpiredErrorPage extends BaseMessagePage {
    protected PageExpiredErrorPage() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	@Override
    protected String getMessage() {
        return "Page Expired: The page you requested has expired.";
    }
}
