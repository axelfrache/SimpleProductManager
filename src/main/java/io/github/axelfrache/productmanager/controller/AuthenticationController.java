package io.github.axelfrache.productmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/custom-redirect")
	public String customRedirectAfterLogin() {
		return "redirect:/homepage";
	}
}
