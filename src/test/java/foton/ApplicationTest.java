/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foton;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import foton.exception.ApiError;
import foton.forms.MailForm;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTest {

	@Autowired
	private MockMvc mockMvc;
	public static final String EMAIL_SUCCESS_MESSAGE = "Your message has been successfully sent. "
			+ "We will contact you very soon!";
//	@Autowired
//	private EmailService emailService;

	protected String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	protected <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	@Test
	public void homePage() throws Exception {
		// N.B. jsoup can be useful for asserting HTML content
		mockMvc.perform(get("/home.html")).andExpect(status().isOk());
	}

	@Test
	public void contactUs() throws Exception {

		String uri = "/send-email";
		MailForm mailForm = new MailForm();
		mailForm.setSenderFullName("test5");
		mailForm.setSenderEmail("test123456@gmail.com");
		mailForm.setSenderPhone("1234567891");
		mailForm.setMessage("message test5");

		// Mail mail = DTOUtil.map(mailForm, Mail.class);
		// this.emailService.sendHtmlMail(mail);

		String inputJson = this.mapToJson(mailForm);
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		MockHttpServletResponse response = mvcResult.getResponse();
		int status = response.getStatus();
		String jsonContent = response.getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		ApiError map = mapper.readValue(jsonContent, ApiError.class);

		assertEquals(HttpStatus.OK.value(), status);
		assertEquals(EMAIL_SUCCESS_MESSAGE, map.getMessage());
	}

}
