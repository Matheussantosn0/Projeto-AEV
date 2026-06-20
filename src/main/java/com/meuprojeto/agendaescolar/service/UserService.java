package com.meuprojeto.agendaescolar.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.meuprojeto.agendaescolar.repository.UsuariosRepository;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


@Service //classe de serviço, onde ficam as regras de negócio

public class UserService {
    
    @Autowired //injeção de dependência, para usar o UserRepository sem precisar instanciar
    private UsuariosRepository userRepository; //variável para acessar os métodos do UserRepository

    public void cadastrarUsuario(Usuarios usuario) { //método para cadastrar um usuário, recebe um objeto do tipo Usuarios como parâmetro
        //lógica para cadastrar um usuário, como validação de dados, criptografia de senha, etc.
       userRepository.inserirUsuario(
            usuario.getNomeCompleto(),
            usuario.getEmail(),
            usuario.getTipoUsuario().name(),
            usuario.getSenha(),
            usuario.getTelefone()
        );

    }

    public Usuarios buscarPorEmail(String email) { //método para buscar um usuário pelo email, recebe o email como parâmetro
        return userRepository.findByEmail(email).orElse(null); //chama o método do repositório para buscar o usuário pelo email, se não encontrar retorna null
    }

    public Usuarios atualizarTelefone(UUID idUsuario, String telefone) { //método para atualizar o telefone de um usuário, recebe o id do usuário e o novo telefone como parâmetros
        Usuarios usuario = userRepository.findById(idUsuario). orElseThrow(() -> new RuntimeException("Usuário não encontrado")); //busca o usuário pelo id, se não encontrar lança uma exceção
        usuario.setTelefone(telefone); //atualiza o telefone do usuário
        return userRepository.save(usuario); //salva as alterações no repositório
    }

    public Usuarios autenticarUsuario(String email, String senha) { //método para autenticar um usuário, recebe o email e a senha como parâmetros
        //lógica para autenticar um usuário, como verificar se o email existe, comparar a senha criptografada, etc.

        Usuarios usuario = buscarPorEmail(email); //chama o método para buscar o usuário pelo email, se não encontrar retorna null
        if (usuario != null && verificarSenha(senha, usuario.getSalt(), usuario.getSenha())) {//verifica se o usuário existe e se a senha digitada é igual à senha armazenada, usando o método verificarSenha
            return usuario;
        }
        return null;
    }

    //método para verificar se a senha digitada é igual à senha armazenada, usando o salt para criptografar a senha digitada e comparar com a senha armazenada
    private boolean verificarSenha(String senhaDigitada,String salt, String senhaArmazenada) {
       
        String senhaComSalt = senhaDigitada + salt; //concatena o salt com a senha digitada

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senhaComSalt.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String senhaCriptografada = sb.toString();
            return senhaCriptografada.equals(senhaArmazenada);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao verificar senha", e);
        }
    }

}
