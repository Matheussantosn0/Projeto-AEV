package com.meuprojeto.agendaescolar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String Login() {
        return "login";
    }
    
        @GetMapping("/cadastro")
        public String Cadastro() {
            return "cadastro";
    }
}