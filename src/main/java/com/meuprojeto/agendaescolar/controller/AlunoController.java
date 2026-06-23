package com.meuprojeto.agendaescolar.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.meuprojeto.agendaescolar.service.AvisosService;
import org.springframework.ui.Model;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.TurmasService;
import com.meuprojeto.agendaescolar.entity.Avisos;
import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Turmas;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private TurmasService turmasService;

    @Autowired
    private AvisosService avisosService;

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
    public String calendarioAluno(HttpSession session, Model model, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        List<Turmas> turmas = turmasService.listarTurmasPorAluno(usuario.getId());
        model.addAttribute("turmas", turmas);

        return "calendario-aluno";
    }

    @GetMapping("/minhas-atividades")
    public String atividadesAluno(HttpSession session, Model model, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        List<Turmas> turmas = turmasService.listarTurmasPorAluno(usuario.getId());
        model.addAttribute("turmas", turmas);
        return "atividades-alunos";
    }

    @GetMapping("/meus-boletins")
    public String boletimAluno(HttpSession session, Model model, HttpServletResponse response) {
        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        List<Turmas> turmas = turmasService.listarTurmasPorAluno(usuario.getId());
        model.addAttribute("turmas", turmas);
        return "boletim-aluno";
    }

    @GetMapping("/avisos")
    @ResponseBody
    public ResponseEntity<?> avisosDaTurma(@RequestParam UUID turmaId, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return ResponseEntity.status(401).build();
        }
        List<Avisos> avisos = avisosService.listarAvisosPorTurma(turmaId);
        return ResponseEntity.ok(avisos);
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

    @PostMapping("/anotacao")
    @ResponseBody
    public ResponseEntity<?> salvarAnotacao(@RequestParam String titulo,
            @RequestParam String dataEvento,
            HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ALUNO) {
            return ResponseEntity.status(401).build();
        }
        avisosService.criarAnotacao(usuario.getId(), null, titulo, LocalDate.parse(dataEvento));
        return ResponseEntity.ok().build();
    }
}
