package com.meuprojeto.agendaescolar.repository;

import java.time.Year;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.meuprojeto.agendaescolar.entity.Turmas;

@Repository
public interface TurmasRepository extends JpaRepository<Turmas, UUID> {

    @Procedure(procedureName = "inserir_turma")
    void inserirTurma(

            UUID p_usuario_id,
            String p_nome,
            Year p_ano_letivo

    );

    @Query("SELECT t FROM Turmas t JOIN ProfessoresTurmas pt ON t.id = pt.turmasId WHERE pt.professorId = :professorId")
    List<Turmas> findByProfessorId(@Param("professorId") UUID professorId);

    void deleteByCodigoAcesso(String codigoAcesso);
}
