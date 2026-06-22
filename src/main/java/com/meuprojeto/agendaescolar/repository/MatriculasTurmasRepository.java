package com.meuprojeto.agendaescolar.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meuprojeto.agendaescolar.entity.MatriculasTurmas;

@Repository
public interface MatriculasTurmasRepository extends JpaRepository<MatriculasTurmas, UUID> {

    // Busca todas as matrículas de um aluno pelo seu ID
    List<MatriculasTurmas> findByAlunosId(UUID alunosId);
}