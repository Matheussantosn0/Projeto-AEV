package com.meuprojeto.agendaescolar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meuprojeto.agendaescolar.entity.TipoUsuario;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/responsavel")
public class ResponsavelController {
    
        @GetMapping("/pagina-do-responsavel")
        public String paginaResponsavel(HttpSession session) {

                Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

                if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
                    return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
                }

                return "navbar-responsavel"; // redireciona para a página do responsavel
        }

        @GetMapping("/agenda-do-aluno")
        public String calendarioResponsavel(HttpSession session) {

                  Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

                if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
                    return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
                }

                return "calendario-responsavel"; // redireciona para a página do responsavel
        }

        @GetMapping("/desempenho-do-aluno")
        public String avaliacoesResponsavel(HttpSession session) {

                 Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

                if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
                    return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
                }

                return "avaliacoes-responsavel";
        }

        @GetMapping("/boletins-do-aluno")
        public String boletimResponsavel(HttpSession session) {
            Usuarios usuario = (Usuarios) session.getAttribute("usuariologado");

            if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.RESPONSAVEL) {
                return "redirect:/login"; // redireciona para a página de login com um parâmetro de erro
            }

            return "boletim-responsavel";
        }
}
