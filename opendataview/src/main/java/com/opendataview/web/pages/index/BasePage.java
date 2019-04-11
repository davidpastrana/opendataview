package com.opendataview.web.pages.index;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendataview.web.pages.contact.AboutPage;
import com.opendataview.web.pages.contact.ContactPage;
import com.opendataview.web.pages.locations.LocationServicePage;
import com.opendataview.web.pages.locations.SamplesPage;
import com.opendataview.web.pages.properties.SetPropertiesPage;
import com.opendataview.web.pages.user.LoginUserPage;
import com.opendataview.web.pages.user.LogoutUserPage;
import com.opendataview.web.pages.user.RegisterUserPage;
import com.opendataview.web.panels.CopyRightPanel;
import com.opendataview.web.panels.FooterPanel;
import com.opendataview.web.panels.TopBarPanel;

import de.agilecoders.wicket.core.markup.html.themes.bootstrap.BootstrapCssReference;

public class BasePage extends WebPage {

	private static final long serialVersionUID = 1L;

	private final static Logger log = LoggerFactory.getLogger(BasePage.class);

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(CssHeaderItem.forReference(BootstrapCssReference.instance()));
		response.render(
				CssHeaderItem.forReference(new PackageResourceReference(BasePage.class, "opendataview.main.css")));
		response.render(CssHeaderItem.forReference(new PackageResourceReference(BasePage.class, "styles.css")));
	}

	public void MemoryConsumed() {
		int mb = 1048576;
		Runtime runtime = Runtime.getRuntime();
		log.info("##### Heap utilization statistics [MB] #####");
		log.info("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		log.info("Free Memory:" + runtime.freeMemory() / mb);
		log.info("Total Memory:" + runtime.totalMemory() / mb);
		log.info("Max Memory:" + runtime.maxMemory() / mb);
	}

	public BasePage() {

		// Session.get().error(getMessage());

		// add(new Label("menuItem", getMenuPageClass()));
		add(new TopBarPanel("topBar"));
		add(new FooterPanel("footer"));
		add(new CopyRightPanel("copyright"));

		add(new HeaderResponseContainer("footer-container", "footer-container"));

		final BookmarkablePageLink<Object> locationsLink = new BookmarkablePageLink<Object>("locationsLink",
				LocationServicePage.class);
		if (locationsLink.linksTo(getPage())) {
			locationsLink.add(new AttributeModifier("class", "active"));
		}
		add(locationsLink);

		final BookmarkablePageLink<Object> samplesLink = new BookmarkablePageLink<Object>("samplesLink",
				SamplesPage.class);
		if (samplesLink.linksTo(getPage())) {
			samplesLink.add(new AttributeModifier("class", "active"));
		}
		add(samplesLink);

		final BookmarkablePageLink<Object> configLink = new BookmarkablePageLink<Object>("configLink",
				SetPropertiesPage.class);
		if (configLink.linksTo(getPage())) {
			configLink.add(new AttributeModifier("class", "active"));
		}
		configLink.setVisible(false);
		add(configLink);

		final BookmarkablePageLink<Object> aboutLink = new BookmarkablePageLink<Object>("aboutUsLink", AboutPage.class);
		if (aboutLink.linksTo(getPage())) {
			aboutLink.add(new AttributeModifier("class", "active"));
		}
		add(aboutLink);

		final BookmarkablePageLink<Object> contactLink = new BookmarkablePageLink<Object>("contactLink",
				ContactPage.class);
		if (contactLink.linksTo(getPage())) {
			contactLink.add(new AttributeModifier("class", "active"));
		}
		add(contactLink);

		final BookmarkablePageLink<Object> loginLink = new BookmarkablePageLink<Object>("loginLink",
				LoginUserPage.class);
		if (loginLink.linksTo(getPage())) {
			loginLink.add(new AttributeModifier("class", "active"));
		}
		add(loginLink);

		final BookmarkablePageLink<Object> logoutLink = new BookmarkablePageLink<Object>("logoutLink",
				LogoutUserPage.class);
		if (logoutLink.linksTo(getPage())) {
			logoutLink.add(new AttributeModifier("class", "active"));
		}
		add(logoutLink);
		logoutLink.setVisible(false);

		final BookmarkablePageLink<Object> registerLink = new BookmarkablePageLink<Object>("registerLink",
				RegisterUserPage.class);
		if (registerLink.linksTo(getPage())) {
			registerLink.add(new AttributeModifier("class", "active"));
		}
		add(registerLink);

		WebSession session = WebSession.get();

		if (session.getAttribute("user_name") != null) {
			registerLink.setVisible(false);
			loginLink.setVisible(false);
			logoutLink.setVisible(true);
			configLink.setVisible(true);
		} else {
			logoutLink.setVisible(false);
		}
	}

	protected String getMessage() {
		return null;
	}

	protected Class<? extends BasePage> getMenuPageClass() {
		return getClass();
	}

}
