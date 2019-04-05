package foton.service;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import foton.model.Mail;

/**
 * Service to send mails
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService {

	private static final String EMAIL_TEXT_TEMPLATE_NAME = "mail/text/email-text";
	private static final String EMAIL_SIMPLE_TEMPLATE_NAME = "mail/html/email-simple";

	private static final String EMAIL_WITHATTACHMENT_TEMPLATE_NAME = "mail/html/email-withattachment";

	static final String USER_VERIFICATION_EMAIL_SUBJECT = "mail.user.verification.subject";
	static final String USER_VERIFICATION_EMAIL_FROM = "mail.user.verification.from";
	static final String USER_VERIFICATION_EMAIL_GREETING = "mail.user.verification.greeting";

	private static final String EMAIL_SITE = "mail.site.email";

	private final JavaMailSender mailSender;
	private final TemplateEngine htmlTemplateEngine;
	private final TemplateEngine textTemplateEngine;
	private final Environment environment;

	@Autowired
	public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine htmlTemplateEngine,
			TemplateEngine textTemplateEngine, Environment environment) {

		this.mailSender = mailSender;
		this.htmlTemplateEngine = htmlTemplateEngine;
		this.textTemplateEngine = textTemplateEngine;
		this.environment = environment;
	}

	/**
	 * Send a simple HTML mail
	 */
	@Override
	public void sendHtmlMail(final Mail mailForm) throws MessagingException {

		// Prepare the evaluation context
		final Context ctx = new Context(mailForm.getLocale());
		// ctx.setVariable("senderName", mailForm.getSenderName());
		// ctx.setVariable("senderLastName", mailForm.getSenderLastName());
		ctx.setVariable("senderFullName", mailForm.getSenderFullName());
		ctx.setVariable("senderEmail", mailForm.getSenderEmail());
		ctx.setVariable("senderPhone", mailForm.getSenderPhone());
		ctx.setVariable("message", mailForm.getMessage());
		ctx.setVariable("subscriptionDate", new Date());
		
		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

		if (mailForm.getSubject() == null) {
			message.setSubject("Contact-us sent from the HOME VIEW.");
		} else {
			message.setSubject(mailForm.getSubject());
		}

		mailForm.setRecipientEmail(environment.getProperty(EMAIL_SITE));

		message.setFrom(mailForm.getSenderEmail());
		message.setTo(environment.getProperty(EMAIL_SITE));
		

		// Create the HTML body using Thymeleaf
		final String htmlContent = this.htmlTemplateEngine.process(EMAIL_SIMPLE_TEMPLATE_NAME, ctx);
		message.setText(htmlContent, true);

		// Send email
		this.mailSender.send(mimeMessage);
	}

	/**
	 * Send a simple TEXT mail
	 */
	@Override
	public void sendTextMail(final Mail mailForm) throws MessagingException {

		// Prepare the evaluation context
		final Context ctx = new Context(mailForm.getLocale());
		ctx.setVariable("senderEmail", mailForm.getSenderEmail());
		ctx.setVariable("senderName", mailForm.getSenderName());
		ctx.setVariable("subscriptionDate", new Date());

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		message.setSubject(mailForm.getSubject());
		message.setFrom(mailForm.getSenderEmail());
		message.setTo(mailForm.getRecipientEmail());

		// Create the plain TEXT body using Thymeleaf
		final String textContent = this.textTemplateEngine.process(EMAIL_TEXT_TEMPLATE_NAME, ctx);
		message.setText(textContent);

		// Send email
		this.mailSender.send(mimeMessage);
	}

	/**
	 * Send HTML mail with attachment.
	 */
	@Override
	public void sendHtmlMailWithAttachment(final String recipientName, final String recipientEmail,
			final String attachmentFileName, final byte[] attachmentBytes, final String attachmentContentType,
			final Locale locale) throws MessagingException {

		// Prepare the evaluation context
		final Context ctx = new Context(locale);
		ctx.setVariable("name", recipientName);
		ctx.setVariable("subscriptionDate", new Date());
		ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
		message.setSubject("Example HTML email with attachment");
		message.setFrom("thymeleaf@example.com");
		message.setTo(recipientEmail);

		// Create the HTML body using Thymeleaf
		final String htmlContent = this.htmlTemplateEngine.process(EMAIL_WITHATTACHMENT_TEMPLATE_NAME, ctx);
		message.setText(htmlContent, true /* isHtml */);

		// Add the attachment
		final InputStreamSource attachmentSource = new ByteArrayResource(attachmentBytes);
		message.addAttachment(attachmentFileName, attachmentSource, attachmentContentType);

		// Send mail
		this.mailSender.send(mimeMessage);
	}

}