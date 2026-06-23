package com.meuprojeto.agendaescolar.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "avisos")
public class Avisos {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @Column(name = "autor_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID autorId;

    @Column(name = "turmas_id", columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID turmasId;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "conteudo", nullable = false)
    private String conteudo;

    @Column(name = "data_evento")
    private LocalDate dataEvento;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Avisos() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public void setAutorId(UUID autorId) {
        this.autorId = autorId;
    }

    public UUID getTurmasId() {
        return turmasId;
    }

    public void setTurmasId(UUID turmasId) {
        this.turmasId = turmasId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}