package com.meuprojeto.agendaescolar.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.meuprojeto.agendaescolar.entity.Avisos;

@Repository
public interface AvisosRepository extends JpaRepository<Avisos, UUID> {
    List<Avisos> findByTurmasId(UUID turmasId);
}