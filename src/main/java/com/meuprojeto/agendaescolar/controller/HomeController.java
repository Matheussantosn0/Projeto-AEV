package com.meuprojeto.agendaescolar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
        @GetMapping("/")
        public String index() {
            return "index";
        }
        
        @GetMapping("/informações-escola")
        public String informacoesEscola() {
            return "informacoes-escola";
        }
}
