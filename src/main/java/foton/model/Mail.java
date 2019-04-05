package foton.model;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Mail {

	@NotEmpty(message = "{email.notempty}")
	@Email
	private String senderEmail;

	private String senderName;

	private String senderLastName;

	private String senderFullName;

	private int senderPhone;

	@Email(message = "Email should be valid!")
	private String recipientEmail;

	private String subject;

	private String message;

	private List<Object> attachments;

	private Map<String, Object> model;

	private Locale locale;

}
