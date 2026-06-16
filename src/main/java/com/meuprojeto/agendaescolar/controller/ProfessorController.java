package com.meuprojeto.agendaescolar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @GetMapping("/pagina-do-professor")
    public String paginaProfessor(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
        }

        return "navbar-professor"; // redireciona para a página do professor
    }

    @GetMapping("/minha-agenda-professor")
    public String calendarioProfessor(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
        }

        return "calendario-professor";
    }

    @GetMapping("/enviar-atividades")
    public String atividadesProfessor(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
        }
        return "atividades-professor";
    }

    @GetMapping("/lançar-boletins")
    public String boletimProfessor(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
        }
        return "boletim-professor";
    }

}
