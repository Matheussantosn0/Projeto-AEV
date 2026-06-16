package com.meuprojeto.agendaescolar.controller;

import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // registra a classe como um controlador, responsável por receber as requisições
            // HTTP e retornar as respostas
public class UserController {

    @Autowired
    private UserService userService; // injeta o serviço para acessar a lógica de negócio

    @PostMapping("/cadastro") // define o caminho para acessar o método de criação de usuário
    public String cadastrarUsuario(@ModelAttribute Usuarios user) { // recebe os dados do usuário no corpo da requisição
        // lógica para cadastrar usuário
        userService.cadastrarUsuario(user); // chama o serviço responsável por cadastrar o usuário
        return "redirect:/login"; // retorna o usuário cadastrado
    }

    @PostMapping("/login") // define o caminho para acessar o método de login
    public String loginUsuario(@RequestParam String email, @RequestParam String senha, HttpSession session) { // recebe
                                                                                                              // os
                                                                                                              // dados
                                                                                                              // do                                                                                                      // usuário
        // no corpo da requisição
        // lógica para autenticar usuário

        Usuarios usuario = userService.autenticarUsuario(email, senha); // chama o serviço responsável por autenticar o
                                                                        // usuário, passando o email e a senha como
                                                                        // parâmetros

        if (usuario == null) { // verifica se o usuário foi encontrado e autenticado
            return "redirect:/login?error=true"; // retorna para a página de login com um parâmetro de erro
        }

        session.setAttribute("usuariologado", usuario);

        switch (usuario.getTipoUsuario()) {

            case ALUNO:
                return "redirect:/aluno/pagina-do-aluno";

            case PROFESSOR:
                return "redirect:/professor/pagina-do-professor";

            case RESPONSAVEL:
                return "redirect:/responsavel/pagina-do-responsavel";

            default:
                return "redirect:/login?error=true"; // retorna para a página de login com um parâmetro de erro se o
                                                     // tipo de
            // usuário for inválido
        }

    }

    @GetMapping("/logout") // define o caminho para acessar o método de logout
    public String logoutUsuario(HttpSession session) { // recebe a sessão do usuário como parâmetro
        session.invalidate(); // invalida a sessão, ou seja, desloga o usuário
        return "redirect:/login"; // redireciona para a página de login
    }
}
