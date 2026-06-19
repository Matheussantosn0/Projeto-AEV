package com.meuprojeto.agendaescolar.controller;

import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import com.meuprojeto.agendaescolar.service.UserService;

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
                                                                                                              // do //
                                                                                                              // usuário
        // no corpo da requisição
        // lógica para autenticar usuário

        Usuarios usuario = userService.autenticarUsuario(email, senha); // chama o serviço responsável por autenticar o
                                                                        // usuário, passando o email e a senha como
                                                                        // parâmetros

        if (usuario == null) { // verifica se o usuário foi encontrado e autenticado
            return "redirect:/login?error=true"; // retorna para a página de login com um parâmetro de erro
        }

        session.setAttribute("usuarioLogado", usuario);

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

    @GetMapping("/api/usuario") // define o caminho para acessar o método de obtenção de usuário
    @ResponseBody // indica que o retorno do método deve ser convertido para JSON e enviado no
                  // corpo da resposta
    public Usuarios getUsuarioLogado(HttpSession session) { // recebe a sessão do usuário como parâmetro
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado"); // obtém o usuário logado a partir da
                                                                             // sessão
        return usuario; // retorna o nome completo do usuário
                        // logado ou uma mensagem caso nenhum
                        // usuário esteja logado
    }

    @PostMapping("/agendaescolar/api/usuario/telefone") // define o caminho para acessar o método de atualização de
                                                        // telefone
    public String atualizarTelefone(@RequestParam String telefone, HttpSession session) { // recebe o novo telefone e a
                                                                                          // sessão do usuário como
                                                                                          // parâmetros
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLogado"); // obtém o usuário logado a partir da
                                                                             // sessão
        if (usuario != null) { // verifica se o usuário está logado

            Usuarios usuarioAtualizado =
            userService.atualizarTelefone(usuario.getId(), telefone); // chama o serviço responsável por atualizar o
                                                                      // telefone do usuário, passando o id do usuário e
                                                                      // o novo telefone como parâmetros
            session.setAttribute("usuarioLogado", usuarioAtualizado);

            return "redirect:/escola/configurar-perfil?success=true"; // redireciona para a página de configuração de perfil
                                                                 // com um parâmetro de sucesso
        }
        return "redirect:/login?error=true"; // redireciona para a página de login com um parâmetro de erro se o usuário não estiver logado
    }
}
