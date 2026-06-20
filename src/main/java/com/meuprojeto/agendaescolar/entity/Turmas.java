package com.meuprojeto.agendaescolar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.Year;

@Entity
@Table(name = "turmas")
public class Turmas {

    @Id // define como chave primária
    @Column(columnDefinition = "BINARY(16)") // UUID armazenado em formato binário
    private UUID id;

    @Column(name = "usuarios_id", nullable = false)
    private UUID usuariosId;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "codigo_acesso", nullable = false, unique = true)
    private String codigoAcesso;

    @Column(name = "ano_letivo", nullable = false)
    private Year anoLetivo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Turmas() {
    } // construtor vazio para o JPA

    public Turmas(UUID usuariosId, String nome, String codigoAcesso, Year anoLetivo) { // construtor para criar uma
                                                                                       // turma
        this.usuariosId = usuariosId;
        this.nome = nome;
        this.codigoAcesso = codigoAcesso;
        this.anoLetivo = anoLetivo;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuariosId() {
        return usuariosId;
    }

    public void setUsuariosId(UUID usuariosId) {
        this.usuariosId = usuariosId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    public Year getAnoLetivo() {
        return anoLetivo;
    }

    public void setAnoLetivo(Year anoLetivo) {
        this.anoLetivo = anoLetivo;
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
