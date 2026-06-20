package com.meuprojeto.agendaescolar.repository;

import java.time.Year;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import com.meuprojeto.agendaescolar.entity.Turmas;

public interface TurmasRepository extends JpaRepository<Turmas, UUID> {

    @Procedure(procedureName = "inserir_turma")
    void inserirTurma(

            UUID p_usuario_id,
            String p_nome,
            Year p_ano_letivo

    );

}
