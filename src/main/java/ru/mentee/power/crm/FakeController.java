package ru.mentee.power.crm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FakeController {

    @GetMapping("/test")
    public ModelAndView test() {
        return new ModelAndView("test");
    }
}
