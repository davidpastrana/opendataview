package com.opendataview.web.pages.index;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.mapper.CryptoMapper;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.htmlcompressor.HtmlCompressingMarkupFactory;
import org.wicketstuff.rest.lambda.mounter.LambdaRestMounter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.openjson.JSONObject;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.opendataview.web.pages.contact.AboutUsPage;
import com.opendataview.web.pages.contact.ContactPage;
import com.opendataview.web.pages.error.AccessDeniedPage;
import com.opendataview.web.pages.error.ErrorPage404;
import com.opendataview.web.pages.error.InternalErrorPage;
import com.opendataview.web.pages.error.PageExpiredErrorPage;
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
import de.agilecoders.wicket.webjars.WicketWebjars;


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
//    WicketWebjars.install(this);
    

//    
//    Map<String, Object> map = new HashMap<>();
//    map.put("integer", 123);
//    map.put("string", "message");
//    
//    LambdaRestMounter restMounter = new LambdaRestMounter(this);
//    
//    //return plain string
//    restMounter.get("/api", (attributes) -> "hello!", Object::toString);
//    //specify a function to transform the returned object into text (json in this case)
//
//    
//    //return id value from url segment
//    restMounter.options("/api/${id}", (attributes) -> {
//        PageParameters pageParameters = attributes.getPageParameters();
//        return pageParameters.get("id");
//       }
//    , Object::toString);
//    LambdaRestMounter mounter = new LambdaRestMounter(this);
//    mounter.get("/", (attributes) -> attributes.getWebResponse().write("bravo!"));
    
    mountPage("/home/#{usr}", getHomePage());
    //mount(new MountedMapperWithoutPageComponentInfo("/", getHomePage()));
    mountPage("/config/#{usr}", SetPropertiesPage.class);
    mountPage("/about", AboutUsPage.class);
    mountPage("/contact", ContactPage.class);
    mountPage("/register", RegisterUserPage.class);
    mountPage("/login", LoginUserPage.class);
    mountPage("/logout", LogoutUserPage.class);
    mountPage("/error", InternalErrorPage.class);
    mountPage("/access-denied", AccessDeniedPage.class);
    mountPage("/session-expired", PageExpiredErrorPage.class);
    mountResource("sitemap.xml", new SitemapPage());
    
    getDebugSettings().setAjaxDebugModeEnabled(false);
    getComponentInstantiationListeners().add(new SpringComponentInjector(this));
    getResourceSettings().getResourceFinders().add(
        new WebApplicationPath(getServletContext(), "/src/main/resources/"));
    //getStoreSettings().setInmemoryCacheSize(50);
    //getStoreSettings().setMaxSizePerSession(Bytes.kilobytes(500));
    
    // set error pages
    getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
    getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
    getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
    
    // markup settings
    getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
    getMarkupSettings().setCompressWhitespace(true);
    getMarkupSettings().setStripComments(true);
    getMarkupSettings().setStripWicketTags(true);
    
    // increase request timeout to support long running transactions
    getRequestCycleSettings().setTimeout(Duration.hours(5));
    
    // RTFACT-4619, fixed by patching HeaderBufferingWebResponse
    getRequestCycleSettings().setBufferResponse(false);
    
    // RTFACT-4636
    getPageSettings().setVersionPagesByDefault(false);
    
    
//    IRequestMapper cryptoMapper = new CryptoMapper(getRootRequestMapper(), this);
//    setRootRequestMapper(cryptoMapper);


  }

  @Override
  public RuntimeConfigurationType getConfigurationType() {
    return RuntimeConfigurationType.DEPLOYMENT;
  }
  
  protected Class<? extends WicketApplication> getMenuPageClass() {
	    return getClass();
	}
}
