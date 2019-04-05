package foton.service;

import java.util.Locale;

import javax.mail.MessagingException;

import foton.model.Mail;

public interface EmailService {
	void sendHtmlMail(Mail mailForm) throws MessagingException;
	void sendTextMail(Mail mailForm) throws MessagingException;
	void sendHtmlMailWithAttachment(String recipientName, String recipientEmail, String attachmentFileName,
			byte[] attachmentBytes, String attachmentContentType, Locale locale) throws MessagingException;
}
