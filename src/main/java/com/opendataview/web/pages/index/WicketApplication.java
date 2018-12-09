package com.opendataview.web.pages.index;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.htmlcompressor.HtmlCompressingMarkupFactory;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.opendataview.web.pages.contact.AboutUsPage;
import com.opendataview.web.pages.contact.ContactPage;
import com.opendataview.web.pages.error.ErrorPage404;
import com.opendataview.web.pages.locations.LocationServicePage;
import com.opendataview.web.pages.properties.SetPropertiesPage;
import com.opendataview.web.pages.user.LoginUserPage;
import com.opendataview.web.pages.user.LogoutUserPage;
import com.opendataview.web.pages.user.RegisterUserPage;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.SingleThemeProvider;
import de.agilecoders.wicket.core.settings.ThemeProvider;


public class WicketApplication extends AuthenticatedWebApplication {
	
	  private final static Logger log = LoggerFactory.getLogger(BasePage.class);

	  
  @Override
  public Class<? extends WebPage> getHomePage() {
    return LocationServicePage.class;
  }

  @Override
  protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
    return AppWebSession.class;
  }

  @Override
  protected Class<? extends WebPage> getSignInPageClass() {
    return LoginUserPage.class;
  }

  
  @Override
  public void init() {
    super.init();

    log.info("Uses usesDeploymentConfig: "+usesDeploymentConfig());

	if (usesDeploymentConfig()){
		getMarkupSettings().setMarkupFactory(new HtmlCompressingMarkupFactory());
		HtmlCompressor compressor = new HtmlCompressor();
		compressor.setPreserveLineBreaks(true);
		getMarkupSettings().setMarkupFactory(new HtmlCompressingMarkupFactory(compressor));
	}

    Bootstrap.install(this);
    mountPage("/", LocationServicePage.class);
    mountPage("/config", SetPropertiesPage.class);
    mountPage("/about", AboutUsPage.class);
    mountPage("/contact", ContactPage.class);
    mountPage("/register", RegisterUserPage.class);
    mountPage("/login", LoginUserPage.class);
    mountPage("/logout", LogoutUserPage.class);
    mountPage("/error", ErrorPage404.class);
    
    getDebugSettings().setAjaxDebugModeEnabled(false);
    getComponentInstantiationListeners().add(new SpringComponentInjector(this));
    getResourceSettings().getResourceFinders().add(
        new WebApplicationPath(getServletContext(), "/src/main/resources/"));
    //getStoreSettings().setInmemoryCacheSize(50);
    //getStoreSettings().setMaxSizePerSession(Bytes.kilobytes(500));
    getApplicationSettings().setPageExpiredErrorPage(ErrorPage404.class);
  }

  @Override
  public RuntimeConfigurationType getConfigurationType() {
    return RuntimeConfigurationType.DEPLOYMENT;
  }
}
