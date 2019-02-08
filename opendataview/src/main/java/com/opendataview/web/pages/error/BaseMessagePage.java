package com.opendataview.web.pages.error;

import java.io.IOException;

import org.apache.wicket.Session;

import com.opendataview.web.pages.index.BasePage;
import com.opendataview.web.pages.locations.LocationServicePage;

public abstract class BaseMessagePage extends BasePage {

	private static final long serialVersionUID = 1L;

	protected BaseMessagePage() throws IOException {
        Session.get().error(getMessage());
    }

    @Override
    protected Class<? extends BasePage> getMenuPageClass() {
        return LocationServicePage.class;
    }

    protected abstract String getMessage();
}
