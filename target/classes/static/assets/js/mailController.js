$(document).ready(
		function() {

			// SUBMIT FORM
			$("#contactForm").submit(function(event) {
				// Prevent the form from submitting via the browser.
				event.preventDefault();
				sendEmailFromAPI();
			});

			function sendEmailFromAPI() {

				var mailForm = {
					senderFullName : $("#senderFullName").val(),
					senderEmail : $("#senderEmail").val(),
					message : $("#message").val()
				};
				var requestJSON = JSON.stringify(mailForm);
				// DO POST
				$.ajax({
					type : "POST",
					url : "/send-email",
					headers : {
						"Content-Type" : "application/json",
						"Accept" : "application/json"
					},
					data : requestJSON,
					dataType : 'json',
				// success : function(result, status, xhr) {
				// successMail(result, status, xhr)
				// },
				// error : function(xhr, status, error) {
				// errorMail(xhr, status, error)
				// }
				}).done(function(data) {
					console.log("done: ", data);
					if (data.status === "OK") {
						$("#modalHeader").text("Success!");
						$("#modalContent").text(data.message);
						$('#contactModal').modal('show');
						$('#contactForm')[0].reset();

					} else {
						console.log("ApiError: ", data.message);
						console.log("ApiError_success: ", data.ApiError);
					}
				}).fail(
						function(data) {
							var apiError = JSON.parse(data.responseText);
							var mapErrors = apiError.mapErrors;

							var fullNameError = mapErrors.senderFullName;
							var emailError = mapErrors.senderEmail;
							var messageError = mapErrors.message;

							$("#modalHeader").text(apiError.message);
							$("#modalContent").multiline(fullNameError,
									emailError, messageError);

							// $("#modalContent").multiline(
							// "FullName: " + fullNameError + "\n"
							// + "Email: " + emailError + "\n"
							// + "Message: " + messageError);

							$('#contactModal').modal('show');
							$('#contactForm')[0].reset();
						});
			}

			$.fn.multiline = function(fullNameError, emailError, messageError) {

				console.log("fullNameError: ", fullNameError);
				console.log("emailError: ", emailError);
				console.log("messageError: ", messageError);
				var text;
				if (fullNameError !== undefined && emailError !== undefined
						&& messageError !== undefined) {
					text = "FullName: " + fullNameError + "\n" + "Email: "
							+ emailError + "\n" + "Message: " + messageError;
				} else if (fullNameError === undefined
						&& emailError !== undefined
						&& messageError !== undefined) {
					text = "Email: " + emailError + "\n" + "Message: "
							+ messageError;
				} else if (fullNameError === undefined
						&& emailError !== undefined
						&& messageError === undefined) {
					text = "Email: " + emailError;
				} else if (fullNameError !== undefined
						&& emailError !== undefined
						&& messageError === undefined) {
					text = "FullName: " + fullNameError + "\n" + "Email: "
							+ emailError;
				} else if (fullNameError !== undefined
						&& emailError === undefined
						&& messageError !== undefined) {
					text = "FullName: " + fullNameError + "\n" + "Message: "
							+ messageError;
				} else if (fullNameError === undefined
						&& emailError === undefined
						&& messageError !== undefined) {
					text = "Message: " + messageError;
				}
				console.log("text: ", text);
				this.text(text);
				this.html(this.html().replace(/\n/g, '<br/>'));
				return this;
			}

			function successMail(result, status, xhr) {
				console.log("result:", result.status);
				console.log("status:", status);

				if (result.status == "OK") {
					console.log("status1:", result.status);
					console.log("message1:", result.message);
					$("#modalHeader").text("Success!");
					$("#modalContent").text(result.message);
					$('#contactModal').modal('show');
					$('#contactForm')[0].reset();
				} else if (result.status == "BAD_REQUEST") {
					console.log("status2:", result.status);
					console.log("message2:", result.message);
					$("#modalHeader").text("Error!");
					$("#modalContent").text(result.message);
					$('#contactModal').modal('show');
					$('#contactForm')[0].reset();
				} else {
					// SERVER_ERROR
					console.log("status3:", result.status);
					console.log("message3:", result.message);
					$("#modalContent").text(result.message);
					$('#contactModal').modal('show');
					$('#contactForm')[0].reset();
				}
			}

			function errorMail(xhr, status, error) {
				$("#modalHeader").text("Error Message");
				$("#modalContent").text("error!!!");
				$('#contactModal').modal('show');
				$('#contactForm')[0].reset();
				console.log("xhr_status:", xhr.status);
				console.log("xhr_statusText:", xhr.statusText);
			}
		});