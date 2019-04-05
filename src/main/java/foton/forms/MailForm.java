package foton.forms;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailForm {

	@Email
	private String senderEmail;

	private String senderName;

	private String senderLastName;
	
	private String senderFullName;

	private String senderPhone;

	@Email
	private String recipientEmail;

	private String subject;

	private String message;

	private List<Object> attachments;

	private Map<String, Object> model;

	private Locale locale;

	private boolean checked;

	private String view;

}