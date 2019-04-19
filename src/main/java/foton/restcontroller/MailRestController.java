package foton.restcontroller;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import foton.exception.ApiError;
import foton.forms.MailForm;
import foton.model.Mail;
import foton.service.EmailService;
import foton.utils.DTOUtil;
import foton.validator.MailValidator;

@RestController
public class MailRestController {

	public static final String EMAIL_SUCCESS_MESSAGE = "Your message has been successfully sent. "
			+ "We will contact you very soon!";

	private static final String UNABLE_TO_SEND = "Unable to send email.";
	private static final String INTERNAL_SERVER_ERROR = "The server was unable to complete your request.";

	private final EmailService emailService;
	private final MailValidator mailValidator;
	private final MessageSource messageSource;

	@Autowired
	public MailRestController(EmailService emailService, MailValidator mailValidator, MessageSource messageSource) {
		this.emailService = emailService;
		this.mailValidator = mailValidator;
		this.messageSource = messageSource;
	}

	@InitBinder("mailForm")
	public void initUserBinder(WebDataBinder binder) {
		binder.addValidators(mailValidator);
	}

	/**
	 * Send simple HTML mail contact-us view
	 * 
	 * @throws MessagingException
	 **/
	@PostMapping(path = "/send-email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> contactUs(@Valid @RequestBody final MailForm mailForm, BindingResult bindingResult) {

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		HttpHeaders headers = new HttpHeaders();
		HashMap<String, String> mapErrors = new HashMap<String, String>();
		ResponseEntity<Object> response;

		if (bindingResult.hasErrors()) {
			headers.setLocation(location);
			for (final FieldError error : bindingResult.getFieldErrors()) {
				String errorMessage = messageSource.getMessage(error, Locale.getDefault());
				mapErrors.put(error.getField(), errorMessage);
			}
			for (final ObjectError error : bindingResult.getGlobalErrors()) {
				String errorMessage = messageSource.getMessage(error, Locale.getDefault());
				mapErrors.put(error.getObjectName(), errorMessage);
			}
			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, UNABLE_TO_SEND, mapErrors);
			response = new ResponseEntity<Object>(apiError, headers, HttpStatus.BAD_REQUEST);
		} else {

			Mail mail = DTOUtil.map(mailForm, Mail.class);
			try {
				this.emailService.sendHtmlMail(mail);
				headers.setLocation(location);
				ApiError apiError = new ApiError(HttpStatus.OK, EMAIL_SUCCESS_MESSAGE, mapErrors);
				response = new ResponseEntity<Object>(apiError, headers, HttpStatus.OK);
			} catch (MessagingException messagingException) {
				mapErrors.put(messagingException.getLocalizedMessage(), messagingException.getMessage());
				ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, mapErrors);
				response = new ResponseEntity<Object>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		System.out.println("Response: " + response.toString());
		return response;
	}

	/* Send plain TEXT mail */
	// problem when sent text model (not working)
	@PostMapping(value = "/send-email-text")
	public String sendTextMail(@Valid @ModelAttribute("mailForm") final MailForm mailForm, BindingResult bindingResult)
			throws MessagingException {

		if (bindingResult.hasErrors()) {
			return bindingResult.getAllErrors().toString();
		}

		mailForm.setRecipientEmail("contact@foton-it.com");
		Mail mail = DTOUtil.map(mailForm, Mail.class);
		this.emailService.sendTextMail(mail);
		return EMAIL_SUCCESS_MESSAGE;

	}

	/* Send HTML mail with attachment. */
	@PostMapping("send-email-attachment")
	public String sendMailWithAttachment(@RequestParam("recipientName") final String recipientName,
			@RequestParam("recipientEmail") final String recipientEmail,
			@RequestParam("attachment") final MultipartFile attachment, final Locale locale)
			throws MessagingException, IOException {

		this.emailService.sendHtmlMailWithAttachment(recipientName, recipientEmail, attachment.getOriginalFilename(),
				attachment.getBytes(), attachment.getContentType(), locale);
		return EMAIL_SUCCESS_MESSAGE;

	}

}