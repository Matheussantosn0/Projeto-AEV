package com.meuprojeto.agendaescolar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Turmas;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.AvisosService;
import com.meuprojeto.agendaescolar.service.TurmasService;
import java.util.List;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/responsavel")
public class ResponsavelController {

    @Autowired
    private TurmasService turmasService;

    @Autowired
    private AvisosService avisosService;

    @GetMapping("/pagina-do-responsavel")
    public String paginaResponsavel(HttpSession session, Model model, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
            return "redirect:/login";
        }
        List<Turmas> turmas = turmasService.listarTurmasPorResponsavel(usuario.getId());
        model.addAttribute("turmas", turmas);
        return "navbar-responsavel";
    }

    @GetMapping("/agenda-do-aluno")
    public String calendarioResponsavel(HttpSession session, Model model, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
            return "redirect:/login";
        }

        // Busca as turmas dos filhos vinculados ao responsável
        List<Turmas> turmasFilho = turmasService.listarTurmasPorResponsavel(usuario.getId());

        // Pega o código de acesso da primeira turma (se existir)
        String codigoTurma = turmasFilho.isEmpty() ? null : turmasFilho.get(0).getCodigoAcesso();

        model.addAttribute("turmaFilhoCodigo", codigoTurma);
        model.addAttribute("turmas", turmasFilho);
        model.addAttribute("usuario", usuario);
        return "calendario-responsavel";
    }

    @GetMapping("/desempenho-do-aluno")
    public String avaliacoesResponsavel(HttpSession session, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
            return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
        }

        return "avaliacoes-responsavel";
    }

    @GetMapping("/boletins-do-aluno")
    public String boletimResponsavel(HttpSession session, HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
            return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
        }

        return "boletim-responsavel";
    }

    @PostMapping("/vincular-aluno")
    public String vincularAluno(@RequestParam String emailAluno, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
            return "redirect:/login";
        }
        turmasService.vincularResponsavelAluno(usuario.getId(), emailAluno);
        return "redirect:/responsavel/pagina-do-responsavel";
    }

    @GetMapping("/avisos-filho")
    @ResponseBody
    public ResponseEntity<?> avisosDoFilho(@RequestParam String turmaId, HttpSession session) {
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
            return ResponseEntity.status(401).build();
        }
        Turmas turma = turmasService.buscarPorCodigo(turmaId).orElse(null);
        if (turma == null)
            return ResponseEntity.badRequest().body("Turma não encontrada");

        return ResponseEntity.ok(avisosService.listarAvisosPorTurma(turma.getId()));
    }
}
