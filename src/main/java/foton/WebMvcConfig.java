package foton;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Spring MVC and Thymeleaf configuration.
 */

@Configuration
@EnableWebMvc
@ComponentScan
public class WebMvcConfig implements ApplicationContextAware, WebMvcConfigurer {

	private static final String MESSAGESOURCE_BASENAME = "spring.messages.basename";
	private static final String MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE = "message.source.use.code.as.default.message";
	private static final String MESSAGESOURCE_ENCODING = "spring.messages.encoding";

	@Autowired
	private Environment environment;

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		WebMvcConfig.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Dispatcher configuration for serving STATIC RESOURCES
	 */
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/assets/**", "/css/**", "/img/**", "/js/**").addResourceLocations(
				"classpath:/static/assets/", "classpath:/static/assets/css/", "classpath:/static/assets/img/",
				"classpath:/static/assets/js/");
	}

	/**
	 * To read messages from message.properties
	 * 
	 * @return //ResourceBundleMessageSource or MessageSource
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		// final ReloadableResourceBundleMessageSource messageSource = new
		// ReloadableResourceBundleMessageSource();
		messageSource.setBasename(environment.getRequiredProperty(MESSAGESOURCE_BASENAME));
		messageSource.setUseCodeAsDefaultMessage(
				Boolean.parseBoolean(environment.getRequiredProperty(MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE)));
		messageSource.setDefaultEncoding(environment.getRequiredProperty(MESSAGESOURCE_ENCODING));
		return messageSource;
	}

	/**
	 * Set validation message source to message.properties
	 * 
	 * @return
	 */
	@Bean(name = "validator")
	@Override
	public LocalValidatorFactoryBean getValidator() {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());
		return bean;
	}

//	@Override
//	public Validator getValidator() {
//		return validator();
//	}

	/**
	 * Multipart resolver (needed for uploading attachments from web form)
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(10485760); // 10MBytes
		return multipartResolver;
	}

	/* **************************************************************** */
	/* THYMELEAF-SPECIFIC ARTIFACTS */
	/* TemplateResolver <- TemplateEngine <- ViewResolver */
	/* **************************************************************** */

	@Bean(name = "templateResolverBean")
	public SpringResourceTemplateResolver templateResolver() {
		// SpringResourceTemplateResolver automatically integrates with Spring's
		// own
		// resource resolution infrastructure, which is highly recommended.
		final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(WebMvcConfig.applicationContext);
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		// HTML is the default value, added here for the sake of clarity.
		templateResolver.setTemplateMode(TemplateMode.HTML);
		// Template cache is true by default. Set to false if you want
		// templates to be automatically updated when modified.
		templateResolver.setCacheable(false);
		return templateResolver;
	}

	/**
	 * A Template engine To serve HTML5 content
	 * 
	 * @return
	 */
	@Primary
	@Bean(name = "templateEngineBean") // using templateResolverBean
	public SpringTemplateEngine templateEngine() {
		final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		templateEngine.addDialect(new SpringSecurityDialect());
		return templateEngine;
	}

	/**
	 * A Thymeleaf template engine with spring integration
	 * 
	 * @return
	 */
	@Bean("viewResolverBean") // using templateEngineBean
	public ThymeleafViewResolver viewResolver() {
		final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}

	/**
	 * Create a REST Template Bean
	 * 
	 * @param builder
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}