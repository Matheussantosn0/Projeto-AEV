package com.meuprojeto.agendaescolar.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import com.meuprojeto.agendaescolar.entity.Usuarios;
import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuarios, UUID> {
    // aqui podemos definir métodos personalizados de consulta, se necessário
    Optional<Usuarios> findByEmail(String email); // exemplo de método para encontrar um usuário pelo email

    @Procedure( procedureName = "inserir_usuario")// exemplo de método para chamar a procedure de inserção de usuário
    void inserirUsuario(
        String p_nomecompleto,
        String p_email,
        String p_tipo_usuario,
        String p_senha,
        String p_telefone
    );
}