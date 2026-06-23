package com.meuprojeto.agendaescolar.controller;

import com.meuprojeto.agendaescolar.entity.Avisos;
import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.TurmasService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.meuprojeto.agendaescolar.entity.Turmas;

import java.time.LocalDate;
import java.time.Year;
import org.springframework.ui.Model;
import java.util.List;
import java.util.UUID;

import com.meuprojeto.agendaescolar.service.AvisosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private AvisosService avisosService;

    private final TurmasService turmasService;

    ProfessorController(TurmasService turmasService) {
        this.turmasService = turmasService;
    }

    @GetMapping("/pagina-do-professor")
    public String paginaProfessor(HttpSession session, Model model, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        List<Turmas> turmas = turmasService.listarTurmasPorProfessor(usuario.getId());
        model.addAttribute("turmas", turmas);

        return "navbar-professor";
    }

    @GetMapping("/minha-agenda-professor")
    public String calendarioProfessor(HttpSession session, Model model) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }
        List<Turmas> turmas = turmasService.listarTurmasPorProfessor(usuario.getId());
        model.addAttribute("turmas", turmas);
        model.addAttribute("usuario", usuario);
        return "calendario-professor";
    }

    @GetMapping("/enviar-atividades")
    public String atividadesProfessor(HttpSession session, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }
        return "atividades-professor";
    }

    @GetMapping("/lançar-boletins")
    public String boletimProfessor(HttpSession session, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }
        return "boletim-professor";
    }

    @GetMapping("/alunos-turma")
    @ResponseBody
    public ResponseEntity<?> alunosPorTurma(@RequestParam String codigo, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return ResponseEntity.status(401).build();
        }
        List<Usuarios> alunos = turmasService.listarAlunosPorTurma(codigo);
        return ResponseEntity.ok(alunos);
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

        Usuarios usuarios = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuarios == null || usuarios.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return "redirect:/login";
        }

        turmasService.excluirTurmaPorCodigo(codigo);

        return "redirect:/professor/pagina-do-professor";
    }

    @PostMapping("/aviso")
    @ResponseBody
    public ResponseEntity<?> criarAviso(
            @RequestParam String turmaId,
            @RequestParam String titulo,
            @RequestParam String conteudo,
            @RequestParam String dataEvento,
            HttpSession session) {

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return ResponseEntity.status(401).build();
        }

        Turmas turma = turmasService.buscarPorCodigo(turmaId).orElse(null);
        if (turma == null) {
            return ResponseEntity.badRequest().body("Turma não encontrada");
        }

        LocalDate data = LocalDate.parse(dataEvento);
        avisosService.criarAviso(usuario.getId(), turma.getId(), titulo, conteudo, data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avisos-turma")
    @ResponseBody
    public ResponseEntity<?> avisosPorTurma(@RequestParam String turmaId, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return ResponseEntity.status(401).build();
        }
        Turmas turma = turmasService.buscarPorCodigo(turmaId).orElse(null);
        if (turma == null)
            return ResponseEntity.badRequest().body("Turma não encontrada");

        List<Avisos> avisos = avisosService.listarAvisosPorTurma(turma.getId());
        return ResponseEntity.ok(avisos);
    }

    @PostMapping("/excluir-aviso/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirAviso(@PathVariable UUID id, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            return ResponseEntity.status(401).build();
        }
        avisosService.excluirAviso(id);
        return ResponseEntity.ok().build();
    }
}
