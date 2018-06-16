package app.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController implements org.springframework.boot.web.servlet.error.ErrorController {
    @GetMapping("/error")
    public String error() {
        return "forward:/index.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
