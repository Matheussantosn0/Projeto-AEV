package com.meuprojeto.agendaescolar.entity;

import java.time.LocalDate;
import java.util.UUID;
import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Table(name = "matriculas_turmas")
public class MatriculasTurmas {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @Column(name = "turmas_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID turmasId;

    @Column(name = "alunos_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID alunosId;

    @Column(name = "data_matricula", nullable = false)
    private LocalDate dataMatricula;

    @Column(name = "status")
    private String status;

    // getters e setters

    public MatriculasTurmas() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTurmasId() { return turmasId; }
    public void setTurmasId(UUID turmasId) { this.turmasId = turmasId; }

    public UUID getAlunosId() { return alunosId; }
    public void setAlunosId(UUID alunosId) { this.alunosId = alunosId; }

    public LocalDate getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(LocalDate dataMatricula) { this.dataMatricula = dataMatricula; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

} 
