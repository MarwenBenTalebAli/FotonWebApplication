package foton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import foton.forms.MailForm;

@Controller
public class HomeController {

    public static final String HOME_VIEW = "home";

	@GetMapping(value = { "/", "/home" })
    public String home(@ModelAttribute MailForm mailForm) {
        return HOME_VIEW;
    }

}
