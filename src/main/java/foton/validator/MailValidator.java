package foton.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import foton.forms.MailForm;

@Component
public class MailValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return MailForm.class.equals(aClass);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		MailForm mailForm = (MailForm) obj;

		// validateSenderName(errors, mailForm);
		validateSenderFullName(errors, mailForm);
		validateSenderEmail(errors, mailForm);
		validateMessage(errors, mailForm);

		validateSenderPhone(errors, mailForm);
		validateSubject(errors, mailForm);

		validateSenderLastName(errors, mailForm);

//		if (mailForm.getView().equals(HomeController.HOME_VIEW)) {
//			validateChecked(errors, mailForm);
//		}
	}

//	private void validateChecked(Errors errors, MailForm mailForm) {
//		if (mailForm.isChecked() == false) {
//			errors.rejectValue("checked", "mailForm.checked");
//		}
//	}

	public void validateSenderEmail(Errors errors, MailForm mailForm) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderEmail", "email.notempty");
	}

	public void validateMessage(Errors errors, MailForm mailForm) {
		if (mailForm.getMessage() != null) {
			if (mailForm.getMessage().isEmpty()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "message", "NotEmpty");
			} else {
				if (mailForm.getMessage().length() < 3 || mailForm.getMessage().length() > 80)
					errors.rejectValue("message", "Size.mailForm.message");
			}
		}
	}

	public void validateSubject(Errors errors, MailForm mailForm) {
		if (mailForm.getSubject() != null) {
			if (mailForm.getSubject().isEmpty()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "NotEmpty");
			} else {
				if (mailForm.getSubject().length() < 3 || mailForm.getSubject().length() > 63)
					errors.rejectValue("subject", "Size.mailForm.subject");
			}
		}
	}

	public void validateSenderPhone(Errors errors, MailForm mailForm) {
		if (mailForm.getSenderPhone() != null) {
			if ((mailForm.getSenderPhone().toString()).isEmpty()) {
				errors.rejectValue("senderPhone", "Size.mailForm.senderPhone");
			} else {

			}
		}
	}

	public void validateSenderName(Errors errors, MailForm mailForm) {
		if (mailForm.getSenderName() != null) {
			if (mailForm.getSenderName().isEmpty()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderName", "NotEmpty");
			} else {
				if (mailForm.getSenderName().length() < 3 || mailForm.getSenderName().length() > 12)
					errors.rejectValue("senderName", "Size.mailForm.senderName");
			}
		}
	}

	public void validateSenderLastName(Errors errors, MailForm mailForm) {
		if (mailForm.getSenderLastName() != null) {
			if (mailForm.getSenderLastName().isEmpty()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderLastName", "NotEmpty");
			} else {
				if (mailForm.getSenderLastName().length() < 3 || mailForm.getSenderLastName().length() > 12)
					errors.rejectValue("senderLastName", "Size.mailForm.senderLastName");
			}
		}
	}

	public void validateSenderFullName(Errors errors, MailForm mailForm) {
		if (mailForm.getSenderFullName() != null) {
			if (mailForm.getSenderFullName().isEmpty()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderFullName", "NotEmpty");
			} else {
				if (mailForm.getSenderFullName().length() < 3 || mailForm.getSenderFullName().length() > 20)
					errors.rejectValue("senderFullName", "Size.mailForm.senderFullName");
			}
		}
	}

}