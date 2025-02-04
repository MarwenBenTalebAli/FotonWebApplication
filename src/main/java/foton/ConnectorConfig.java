package foton;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorConfig {
	
	private final static String SECURITY_USER_CONSTRAINT = "CONFIDENTIAL";
	private final static String REDIRECT_PATTERN = "/*";
	private final static String CONNECTOR_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
	private final static String CONNECTOR_SCHEME = "http";

	@Bean
	public TomcatServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory  tomcat = new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint(SECURITY_USER_CONSTRAINT);
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern(REDIRECT_PATTERN);
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(getHttpConnector());
		return tomcat;
	}

	private Connector getHttpConnector() {
		Connector connector = new Connector(CONNECTOR_PROTOCOL);
		connector.setScheme(CONNECTOR_SCHEME);
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(8443);
		return connector;
	}
}
