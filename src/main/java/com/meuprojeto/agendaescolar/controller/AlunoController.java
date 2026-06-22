package com.meuprojeto.agendaescolar.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.ui.Model;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.TurmasService;
import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Turmas;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private TurmasService turmasService;

    @GetMapping("/pagina-do-aluno")
    public String paginaAluno(HttpSession session, Model model) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
        }

        List<Turmas> turmas = turmasService.listarTurmasPorAluno(usuario.getId());
        model.addAttribute("turmas", turmas);
        return "navbar-aluno"; // redireciona para a página do aluno
    }

    @GetMapping("/minha-agenda-aluno")
    public String calendarioAluno(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }
        return "calendario-aluno";
    }

    @GetMapping("/minhas-atividades")
    public String atividadesAluno(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }
        return "atividades-alunos";
    }

    @GetMapping("/meus-boletins")
    public String boletimAluno(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }
        return "boletim-aluno";
    }

    @PostMapping("/entrar-turma")
    public String entrarTurma(@RequestParam String codigoAcesso, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }
        turmasService.entrarTurmaAluno(usuario.getId(), codigoAcesso);
        return "redirect:/aluno/pagina-do-aluno";
    }

}
