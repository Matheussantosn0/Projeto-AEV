package com.meuprojeto.agendaescolar.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.entity.TipoUsuario;


@Controller
@RequestMapping("/aluno")
public class AlunoController {
    
        @GetMapping("/pagina-do-aluno")
        public String paginaAluno(HttpSession session) {
            
            Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

                if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
                    return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
                } 

                return "navbar-aluno"; // redireciona para a página do aluno
        }
        
        @GetMapping("/minha-agenda-aluno")
        public String calendarioAluno(HttpSession session) {

            Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

            if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
                return "redirect:/login";
            }
            return "calendario-aluno";
        }

        @GetMapping("/minhas-atividades")
        public String atividadesAluno(HttpSession session) {

            Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

            if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
                return "redirect:/login";
            }
            return "atividades-aluno";
        }

        @GetMapping("/meus-boletins")
        public String boletimAluno(HttpSession session) {

            Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

            if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
                return "redirect:/login";
            }
            return "boletim-aluno";
        }
}
