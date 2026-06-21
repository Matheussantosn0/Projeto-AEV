package com.meuprojeto.agendaescolar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "professores_turmas")
public class ProfessoresTurmas {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @Column(name = "turmas_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID turmasId;

    @Column(name = "professor_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID professorId;

    @Column(name = "disciplina")
    private String disciplina;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProfessoresTurmas() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTurmasId() { return turmasId; }
    public void setTurmasId(UUID turmasId) { this.turmasId = turmasId; }

    public UUID getProfessorId() { return professorId; }
    public void setProfessorId(UUID professorId) { this.professorId = professorId; }

    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}