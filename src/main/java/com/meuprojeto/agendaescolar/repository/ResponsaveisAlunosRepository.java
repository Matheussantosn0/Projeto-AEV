package com.meuprojeto.agendaescolar.repository;

import java.util.UUID;
import com.meuprojeto.agendaescolar.entity.ResponsaveisAlunos;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsaveisAlunosRepository extends JpaRepository<ResponsaveisAlunos, UUID> {
    List<ResponsaveisAlunos> findByResponsavelId(UUID responsavelId);
}
