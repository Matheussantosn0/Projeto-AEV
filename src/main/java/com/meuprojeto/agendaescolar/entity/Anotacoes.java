package com.meuprojeto.agendaescolar.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "anotacoes")
public class Anotacoes {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @Column(name = "criador_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID criadorId;

    @Column(name = "turmas_id", columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID turmasId;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Anotacoes() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCriadorId() { return criadorId; }
    public void setCriadorId(UUID criadorId) { this.criadorId = criadorId; }
    public UUID getTurmasId() { return turmasId; }
    public void setTurmasId(UUID turmasId) { this.turmasId = turmasId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public LocalDate getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDate dataEvento) { this.dataEvento = dataEvento; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
