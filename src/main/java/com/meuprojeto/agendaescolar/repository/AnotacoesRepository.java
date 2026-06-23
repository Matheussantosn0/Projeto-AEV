package com.meuprojeto.agendaescolar.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.meuprojeto.agendaescolar.entity.Anotacoes;

@Repository
public interface AnotacoesRepository extends JpaRepository<Anotacoes, UUID> {
    List<Anotacoes> findByCriadorId(UUID criadorId);
}
