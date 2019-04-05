package foton;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:mailConfig/mail.properties")
public class MailConfig {

}