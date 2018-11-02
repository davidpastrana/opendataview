package xyz.spatialdata.flo.web.pages.index;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.WebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.spatialdata.flo.web.pages.contact.AboutUsPage;
import xyz.spatialdata.flo.web.pages.contact.ContactPage;
import xyz.spatialdata.flo.web.pages.locations.LocationServicePage;
import xyz.spatialdata.flo.web.pages.properties.SetPropertiesPage;
import xyz.spatialdata.flo.web.pages.user.LoginUserPage;
import xyz.spatialdata.flo.web.pages.user.LogoutUserPage;
import xyz.spatialdata.flo.web.pages.user.RegisterUserPage;
import xyz.spatialdata.flo.web.panels.CopyRightPanel;
import xyz.spatialdata.flo.web.panels.FooterPanel;
import xyz.spatialdata.flo.web.panels.TopBarPanel;

public class BasePage extends WebPage {

  private static final long serialVersionUID = 1L;

  private final static Logger log = LoggerFactory.getLogger(BasePage.class);

  @Override
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    response.render(OnDomReadyHeaderItem
        .forScript("$('#backgroundLoading').fadeOut(0);$('#showLoading').fadeOut(0);"));
  }

  public void MemoryConsumed() {
    int mb = 1048576;
    Runtime runtime = Runtime.getRuntime();
    System.out.println("##### Heap utilization statistics [MB] #####");
    System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
    System.out.println("Free Memory:" + runtime.freeMemory() / mb);
    System.out.println("Total Memory:" + runtime.totalMemory() / mb);
    System.out.println("Max Memory:" + runtime.maxMemory() / mb);
  }

  public BasePage() {

    add(new TopBarPanel("topBar"));
    add(new FooterPanel("footer"));
    add(new CopyRightPanel("copyright"));

    final BookmarkablePageLink<Object> locationsLink =
        new BookmarkablePageLink<Object>("locationsLink", LocationServicePage.class);
    if (locationsLink.linksTo(getPage())) {
      locationsLink.add(new AttributeModifier("class", "active"));
    }
    add(locationsLink);

    final BookmarkablePageLink<Object> configLink =
        new BookmarkablePageLink<Object>("configLink", SetPropertiesPage.class);
    if (configLink.linksTo(getPage())) {
      configLink.add(new AttributeModifier("class", "active"));
    }
    configLink.setVisible(false);
    add(configLink);

    final BookmarkablePageLink<Object> aboutLink =
        new BookmarkablePageLink<Object>("aboutUsLink", AboutUsPage.class);
    if (aboutLink.linksTo(getPage())) {
      aboutLink.add(new AttributeModifier("class", "active"));
    }
    add(aboutLink);

    final BookmarkablePageLink<Object> contactLink =
        new BookmarkablePageLink<Object>("contactLink", ContactPage.class);
    if (contactLink.linksTo(getPage())) {
      contactLink.add(new AttributeModifier("class", "active"));
    }
    add(contactLink);

    final BookmarkablePageLink<Object> loginLink =
        new BookmarkablePageLink<Object>("loginLink", LoginUserPage.class);
    if (loginLink.linksTo(getPage())) {
      loginLink.add(new AttributeModifier("class", "active"));
    }
    add(loginLink);

    final BookmarkablePageLink<Object> logoutLink =
        new BookmarkablePageLink<Object>("logoutLink", LogoutUserPage.class);
    if (logoutLink.linksTo(getPage())) {
      logoutLink.add(new AttributeModifier("class", "active"));
    }
    add(logoutLink);
    logoutLink.setVisible(false);

    final BookmarkablePageLink<Object> registerLink =
        new BookmarkablePageLink<Object>("registerLink", RegisterUserPage.class);
    if (registerLink.linksTo(getPage())) {
      registerLink.add(new AttributeModifier("class", "active"));
    }
    add(registerLink);

    WebSession session = WebSession.get();

    if (session.getAttribute("user_name") != null) {
      registerLink.setVisible(false);
      loginLink.setVisible(false);
      logoutLink.setVisible(true);

      if (session.getAttribute("user_name").toString().contentEquals("admin")) {
        configLink.setVisible(true);
      }
    } else {
      logoutLink.setVisible(false);
    }
  }
}
