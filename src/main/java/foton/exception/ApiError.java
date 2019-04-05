package foton.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiError {

	private HttpStatus status;
	private String message;
	private List<String> listErrors;
	private HashMap<String, String> mapErrors;

	public ApiError() {
		super();
	}

	public ApiError(final HttpStatus status, final String message, final List<String> listErrors) {
		super();
		this.status = status;
		this.message = message;
		this.listErrors = listErrors;
	}

	public ApiError(final HttpStatus status, final String message, final HashMap<String, String> mapErrors) {
		super();
		this.status = status;
		this.message = message;
		this.mapErrors = mapErrors;
	}

	public ApiError(final HttpStatus status, final String message, final String error) {
		super();
		this.status = status;
		this.message = message;
		listErrors = Arrays.asList(error);
	}
}