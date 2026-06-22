package com.meuprojeto.agendaescolar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Turmas;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.TurmasService;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/escola")

public class EscolaController {

    @GetMapping("/gerenciar-escola")
    public String gerenciarEscola(HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        return "gerenciar-escola";
    }

    @GetMapping("/entrar-escola")
    public String entrarEscola(HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }

        return "entrar-escola";
    }

    @GetMapping("/entrar-turma")
    public String entrarTurma(HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO
                && usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
            return "redirect:/login";
        }

        return "entrar-turma";
    }

    @GetMapping("/gerenciar-turmas")
    public String gerenciarTurmas(HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        return "gerenciar-turmas";
    }

    @GetMapping("/configurar-perfil")
    public String configurarPerfil(HttpSession session, Model model) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);

        return "configuracao-perfil";
    }

    private final TurmasService turmasService;

    EscolaController(TurmasService turmasService) {
        this.turmasService = turmasService;
    }

    @GetMapping("/buscar-turma")
    @ResponseBody
    public ResponseEntity<?> buscarTurma(@RequestParam String codigo, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Turmas> turma = turmasService.buscarPorCodigo(codigo);
        if (turma.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(turma.get());
    }

}