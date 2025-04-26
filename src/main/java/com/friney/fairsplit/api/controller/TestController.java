package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.core.entity.user.User;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/info_user")
    public String infoTest(Principal principal) {
        return principal.getName();
    }
}
