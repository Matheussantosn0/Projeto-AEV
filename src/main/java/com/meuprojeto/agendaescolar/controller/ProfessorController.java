package com.meuprojeto.agendaescolar.controller;

import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.TurmasService;

import jakarta.servlet.http.HttpSession;
import com.meuprojeto.agendaescolar.entity.Turmas;
import java.time.Year;
import org.springframework.ui.Model;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private final TurmasService turmasService;

    ProfessorController(TurmasService turmasService) {
        this.turmasService = turmasService;
    }

    @GetMapping("/pagina-do-professor")
    public String paginaProfessor(HttpSession session, Model model) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        List<Turmas> turmas = turmasService.listarTurmasPorProfessor(usuario.getId());
        model.addAttribute("turmas",turmas);

        return "navbar-professor";
    }

    @GetMapping("/minha-agenda-professor")
    public String calendarioProfessor(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        return "calendario-professor";
    }

    @GetMapping("/enviar-atividades")
    public String atividadesProfessor(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }
        return "atividades-professor";
    }

    @GetMapping("/lançar-boletins")
    public String boletimProfessor(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }
        return "boletim-professor";
    }

    @PostMapping("/criar-turma")
    public String criarTurma(@RequestParam String nome, @RequestParam Year anoLetivo, HttpSession session) {

        Usuarios usuarios = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuarios == null || usuarios.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        turmasService.criarTurma(usuarios.getId(), nome, anoLetivo);

        return "redirect:/professor/pagina-do-professor";
    }

    @PostMapping("/excluir-turma/{codigo}")
        public String excluirTurma(@PathVariable String codigo, HttpSession session) {
            
            Usuarios usuarios= (Usuarios) session.getAttribute("usuarioLogado");

            if (usuarios == null || usuarios.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        turmasService.excluirTurmaPorCodigo(codigo);

                return "redirect:/professor/pagina-do-professor";
        }







}
