package com.opendataview.web.pages.index;

import java.util.EnumSet;

import javax.servlet.SessionTrackingMode;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.javascript.DefaultJavaScriptCompressor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.htmlcompressor.HtmlCompressingMarkupFactory;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.opendataview.web.api.LocationsRestResource;
import com.opendataview.web.pages.contact.AboutPage;
import com.opendataview.web.pages.contact.ContactPage;
import com.opendataview.web.pages.error.AccessDeniedPage;
import com.opendataview.web.pages.error.ErrorPage404;
import com.opendataview.web.pages.error.InternalErrorPage;
import com.opendataview.web.pages.error.PageExpiredErrorPage;
import com.opendataview.web.pages.locations.LocationServicePage;
import com.opendataview.web.pages.locations.SamplesPage;
import com.opendataview.web.pages.properties.SetPropertiesPage;
import com.opendataview.web.pages.user.LoginUserPage;
import com.opendataview.web.pages.user.LogoutUserPage;
import com.opendataview.web.pages.user.RegisterUserPage;

import de.agilecoders.wicket.core.Bootstrap;

public class WicketApplication extends AuthenticatedWebApplication {

	private final static Logger log = LoggerFactory.getLogger(BasePage.class);

	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}

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

		// Avoid DEVELOPMENT with failure checks
		getDebugSettings().setComponentUseCheck(false);

		// Catch runtime exceptions this way:
		getRequestCycleListeners().add(new IRequestCycleListener() {
			@Override
			public IRequestHandler onException(RequestCycle cycle, Exception e) {
				return new RenderPageRequestHandler(new PageProvider(new ErrorPage404(e)));
			}

		});

		log.info("Uses usesDeploymentConfig: " + usesDeploymentConfig());

		if (usesDeploymentConfig()) {
			getMarkupSettings().setMarkupFactory(new HtmlCompressingMarkupFactory());
			getMarkupSettings().setStripWicketTags(true);
			getMarkupSettings().setStripComments(true);
			getMarkupSettings().setCompressWhitespace(true);
			HtmlCompressor compressor = new HtmlCompressor();

			// in case we want a more readable html set to true
			compressor.setPreserveLineBreaks(false);

			getMarkupSettings().setMarkupFactory(new HtmlCompressingMarkupFactory(compressor));
		}
//		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PERSISTENCE");
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
//		try {
//			fullTextEntityManager.createIndexer().startAndWait();
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		Bootstrap.install(this);

		mountResource("/api", new ResourceReference("restReference") {
			private static final long serialVersionUID = 1L;

			LocationsRestResource locationsAPI = new LocationsRestResource();

			@Override
			public IResource getResource() {
				return locationsAPI;
			}
		});

		mountPage("/search", getHomePage());
		mountPage("/samples", SamplesPage.class);
		mountPage("/config/#{usr}", SetPropertiesPage.class);
		mountPage("/about", AboutPage.class);
		mountPage("/contact", ContactPage.class);
		mountPage("/register", RegisterUserPage.class);
		mountPage("/login", LoginUserPage.class);
		mountPage("/logout", LogoutUserPage.class);
		mountPage("/error2", InternalErrorPage.class);
		mountPage("/error", ErrorPage404.class);
		mountPage("/access-denied", AccessDeniedPage.class);
		mountPage("/session-expired", PageExpiredErrorPage.class);
		mountResource("sitemap.xml", new SitemapPage());

		// getApplicationSettings().addPostComponentOnBeforeRenderListener(new
		// StatelessChecker());
		getDebugSettings().setAjaxDebugModeEnabled(false);
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		getResourceSettings().getResourceFinders()
				.add(new WebApplicationPath(getServletContext(), "/src/main/resources/"));
		getStoreSettings().setInmemoryCacheSize(50);
		getResourceSettings().setJavaScriptCompressor(new DefaultJavaScriptCompressor());

		// set cookie mode to keep open the same session id
		getServletContext().setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));

		// set error pages
		getApplicationSettings().setPageExpiredErrorPage(LoginUserPage.class);
		getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
		getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);

		// markup settings
		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
		getMarkupSettings().setCompressWhitespace(true);
		getMarkupSettings().setStripComments(true);
		getMarkupSettings().setStripWicketTags(true);
		getMarkupSettings().setAutomaticLinking(true); // used for <wicket:link>

		// increase request timeout to support long running transactions
		getRequestCycleSettings().setTimeout(Duration.hours(1));

		// RTFACT-4619, fixed by patching HeaderBufferingWebResponse
		getRequestCycleSettings().setBufferResponse(false);

		// RTFACT-4636
		getPageSettings().setVersionPagesByDefault(false);
	}

	protected Class<? extends WicketApplication> getMenuPageClass() {
		return getClass();
	}
}
