package com.mercadodavidv.hauntedreviews.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class AuthorizationServerController {

  @GetMapping("/login")
  String login() {
    return "login";
  }
}
